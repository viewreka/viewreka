/*
 * Copyright 2015-2016 the original author or authors.
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
package org.beryx.viewreka.sql.embedded

import groovy.io.FileType
import groovy.util.logging.Slf4j

import java.sql.SQLException

/**
 * Facade for HSQLDB databases
 */
@Slf4j
class HSQLDB extends EmbeddedDB {
    private static final DRIVER = 'org.hsqldb.jdbc.JDBCDriver'

    HSQLDB(String dbPath, String user, String password) {
        super(DRIVER, dbPath, user, password)
    }

    @Override String getBaseUrl() { "jdbc:hsqldb:$dbPathAndName" }
    @Override String getUrlOptionCreate() { null }
    @Override String getUrlOptionIfExists() { "ifexists=true" }

    @Override
    protected boolean shouldCreateDBOnError(Exception e) {
        return ((e instanceof SQLException) && (e.errorCode == -465))
    }

    void deleteDB() {
        log.info("Deleting the DB $dbPathAndName...")

        int pos = dbPathAndName.replace('\\', '/').lastIndexOf('/')
        String dbName = dbPathAndName.substring(pos+1)
        String dir = (pos > 0) ? dbPathAndName.substring(0, pos) : '.'

        File dbDir = new File(dir)
        if(dbDir.isDirectory()) {
            dbDir.eachFile(FileType.FILES) { f ->
                if(f.name.startsWith("${dbName}.")) {
                    f.delete()
                    if(f.exists()) {
                        throw new SQLException("Cannot delete ${f.absolutePath}")
                    }
                }
            }
        }
    }
}
