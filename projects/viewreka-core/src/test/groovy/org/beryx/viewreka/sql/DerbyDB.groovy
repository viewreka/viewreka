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
package org.beryx.viewreka.sql

import groovy.sql.Sql

class DerbyDB {
    final String url
    final String user
    final String password

    DerbyDB(String dbPath, String user, String password) {
        this.url = "jdbc:derby:$dbPath"
        this.user = user
        this.password = password
    }


    static class Builder {
        final String dbName
        String user = ''
        String password = ''
        final List<String> statements = []

        Builder(String dbName) {
            this.dbName = dbName
        }

        def createDB() {
            File tmpDir = new File(System.properties['java.io.tmpdir'], 'viewreka-test')
            def dbDir = new File(tmpDir, dbName)
            dbDir.deleteDir()
            assert !dbDir.exists()
            def dbPath = dbDir.path.replaceAll('\\\\', '/')
            def url = "jdbc:derby:$dbPath;create=true"
            def driver = 'org.apache.derby.jdbc.EmbeddedDriver'
            Sql sql = Sql.newInstance(url, user, password, driver)
            statements.each { stmt ->
                sql.execute(stmt)
            }
            sql.close()
            return new DerbyDB(dbPath, user, password)
        }

        Builder withUser(String user) {
            this.user = user
            return this
        }


        Builder withPassword(String password) {
            this.password = password
            return this
        }

        /**
         * Adds the SQL statements contained in the string as argument
         * @param stmts the string containing SQL statements separated by lines containing only the {@code /} character
         * @return this builder
         */
        Builder withStatements(String stmts) {
            def splitted = stmts.normalize().split('\\n\\s*/\\s*\\n')
            splitted.each { stmt ->
                stmt = stmt.trim()
                if (stmt.endsWith(';')) stmt = stmt.substring(0, stmt.length() - 1)
                if (stmt) statements << stmt
            }
            return this
        }

        /**
         * Adds the SQL statements contained in the file specified as argument
         * @param sqlFile the file containing SQL statements separated by lines containing only the {@code /} character
         * @return this builder
         */
        Builder withSqlFile(File sqlFile) {
            withStatements(sqlFile.text)
            return this
        }

        /**
         * Adds the SQL statements contained in the file with the path specified as argument
         * @param sqlFilePath the path to the file containing SQL statements separated by lines containing only the {@code /} character
         * @return this builder
         */
        Builder withSqlFile(String sqlFilePath) {
            return withSqlFile(new File(sqlFilePath))
        }
    }

}
