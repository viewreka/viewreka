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
