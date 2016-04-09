/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.viewreka.bundle.repo

import groovy.util.logging.Slf4j
import org.beryx.viewreka.sql.util.DerbyDB

import java.sql.Clob
import java.sql.SQLIntegrityConstraintViolationException

@Slf4j
class DerbyCatalogCache implements CatalogCache {
    static final String CREATION_STATEMENTS = '''
        CREATE TABLE catalog (
             url VARCHAR(1024) PRIMARY KEY,
             lastUpdate TIMESTAMP NOT NULL,
             json CLOB
        );
    '''.stripIndent()

    static final GET_CATALOG = 'select json from catalog where url=?'

    static final PUT_CATALOG = '''
                merge into catalog dst
                using catalog src
                on src.url=?
                when matched then update set lastUpdate=current_timestamp, json=?
                when not matched then insert (url, lastUpdate, json) values(?, current_timestamp, ?)
                '''.stripIndent()

    static final INSERT_CATALOG = '''
                insert into catalog (url, lastUpdate, json)
                values(?, current_timestamp, ?)
                '''

    static final UPDATE_CATALOG = 'update catalog set lastUpdate=current_timestamp, json=? where url=?'

    final DerbyDB cacheDB

    CatalogCache.ErrorReporter errorReporter = {message, t -> log.warn(message, t)}

    DerbyCatalogCache(String cacheDbPath) {
        this.cacheDB = new DerbyDB(cacheDbPath, '', '')
                .withDefaultCreateAndDeleteStrategy()
                .withCreationStatements(CREATION_STATEMENTS)
    }


    @Override
    List<BundleInfo> getEntries(String catalogUrl) {
        List<BundleInfo> entries = null
        try {
           cacheDB.executeSelect(GET_CATALOG, [catalogUrl]) { rs ->
                if(!rs) return null
                if(rs.next()) {
                    Clob jsonClob =  rs.getClob('json')
                    entries = SimpleBundleInfo.fromJsonInputStream(jsonClob.asciiStream)
                }
            }
        } catch (Exception e) {
            errorReporter.report("Failed to retrieve entries for catalog $catalogUrl", e)
        }
        return entries
    }

    @Override
    boolean putEntries(String catalogUrl, List<BundleInfo> entries) {
        CatalogBuilder catalogBuilder = new CatalogBuilder()
        catalogBuilder.entries.addAll(entries)
        def json = catalogBuilder.asJson()

        try {
            //TODO - replace the INSERT_CATALOG / UPDATE_CATALOG hack with PUT_CATALOG
//            cacheDB.executeStatement(PUT_CATALOG, [catalogUrl, json, catalogUrl, json])
            try {
                cacheDB.executeStatement(INSERT_CATALOG, [catalogUrl, json])
            } catch (SQLIntegrityConstraintViolationException e) {
                cacheDB.executeStatement(UPDATE_CATALOG, [json, catalogUrl])
            }
        } catch (Exception e) {
            errorReporter.report("Failed to store entries for catalog $catalogUrl", e)
        }

    }
}
