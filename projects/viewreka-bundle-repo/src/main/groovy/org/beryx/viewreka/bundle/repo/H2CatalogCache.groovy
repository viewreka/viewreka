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
import org.beryx.viewreka.sql.embedded.H2DB

import java.sql.Clob

/**
 * A {@link CatalogCache} backed by an H2 database.
 */
@Slf4j
class H2CatalogCache implements CatalogCache {
    static final String CREATION_STATEMENTS = '''
        CREATE TABLE catalog (
             url VARCHAR(1024) PRIMARY KEY,
             lastUpdate TIMESTAMP NOT NULL,
             json CLOB
        );
    '''.stripIndent()

    static final GET_CATALOG = 'select json from catalog where url=?'

    static final PUT_CATALOG = '''
                merge into catalog(url, lastUpdate, json) key(url)
                values (?, current_timestamp, ?)
                '''.stripIndent()

    final H2DB cacheDB

    CatalogCache.ErrorReporter errorReporter = {message, t -> log.warn(message, t)}

    H2CatalogCache(String cacheDbPath) {
        this.cacheDB = new H2DB(cacheDbPath, '', '')
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
            cacheDB.executeStatement(PUT_CATALOG, [catalogUrl, json])
        } catch (Exception e) {
            errorReporter.report("Failed to store entries for catalog $catalogUrl", e)
        }
    }
}
