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
package org.beryx.viewreka.sql.util

import groovy.sql.Sql
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import groovy.util.logging.Slf4j

import java.sql.ResultSet
import java.sql.SQLException

@Slf4j
abstract class EmbeddedDB {
    final String driver
    final String dbPath
    final String user
    final String password

    final List<String> urlOptions = []
    final List<String> creationStatements = []

    final List<String> sqlStatesForCreateDB = []
    final List<String> sqlStatesForDeleteDB = []

    String urlOptionSeparator = ';'

    abstract String getBaseUrl()
    abstract String getUrlOptionCreate()
    abstract String getUrlOptionIfExists()

    EmbeddedDB(String driver, String dbPath, String user, String password) {
        this.driver = driver
        this.dbPath = dbPath
        this.user = user
        this.password = password
    }

    final EmbeddedDB withUrlOption(String option) {
        urlOptions << option
        this
    }

    final EmbeddedDB withSqlStatesForCreateDB(String... states) {
        this.sqlStatesForCreateDB.addAll(states)
        this
    }

    final EmbeddedDB withSqlStatesForDeleteDB(String... states) {
        this.sqlStatesForDeleteDB.addAll(states)
        this
    }

    final EmbeddedDB withCreationStatement(String creationStatement) {
        this.creationStatements << creationStatement
        this
    }

    final EmbeddedDB withCreationStatementList(List<String> creationStatements) {
        this.creationStatements.addAll(creationStatements)
        this
    }

    final EmbeddedDB withCreationStatements(String creationStatements) {
        this.creationStatements.addAll(splitStatements(creationStatements))
        this
    }

    final EmbeddedDB withCreationStatementFile(File creationStatementsFile){
        this.creationStatements.addAll(splitStatements(creationStatementsFile.text))
        this
    }

    final EmbeddedDB withCreationStatementFile(String creationStatementsFile){
        this.creationStatements.addAll(splitStatements(new File(creationStatementsFile).text))
        this
    }

    static List<String> splitStatements(String sqlStatements) {
        List<String> statements = []
        def splitted = sqlStatements?.normalize()?.split('\\n\\s*/\\s*\\n')
        splitted?.each { stmt ->
            stmt = stmt.trim()
            if (stmt.endsWith(';')) stmt = stmt.substring(0, stmt.length() - 1)
            if (stmt) statements << stmt
        }
        statements
    }

    def execute(@ClosureParams(value=SimpleType, options="groovy.sql.Sql") Closure closure) {
        Sql sql = internalGetSql()
        if(sql) {
            try {
                closure.call(sql)
            } finally {
                sql.close()
            }
        }
    }

    def executeStatements(String statements) {
        execute {Sql sql -> splitStatements(statements).each {stmt -> sql.execute(stmt)}}
    }

    def executeStatement(String query, Closure closure = null) {
        execute {Sql sql -> closure ? sql.execute(query, closure) : sql.execute(query)}
    }

    def executeStatement(String query, List params, Closure closure = null) {
        execute {Sql sql ->
            closure ? sql.execute(query, params, closure) : sql.execute(query, params)
        }
    }

    def executeSelect(String query, @ClosureParams(value=SimpleType, options="java.sql.ResultSet")  Closure closure) {
        execute {Sql sql -> sql.query(query, { ResultSet rs -> closure.call(rs)})}
    }

    def executeSelect(String query, List params, @ClosureParams(value=SimpleType, options="java.sql.ResultSet") Closure closure) {
        execute {Sql sql -> sql.query(query, params, { ResultSet rs -> closure.call(rs)})}
    }

    def openConnection() {
        internalGetSql().connection
    }

    protected boolean shouldCreateDBOnError(Exception e) {
        if(e instanceof SQLException) {
            String sqlState = ((SQLException)e).getSQLState()
            return sqlStatesForCreateDB.contains(sqlState)
        }
        return false
    }

    protected boolean shouldDeleteDBDirectoryOnError(Exception e) {
        if(e instanceof SQLException) {
            String sqlState = ((SQLException)e).getSQLState()
            return sqlStatesForDeleteDB.contains(sqlState)
        }
        return false
    }

    protected String getUrlForCreationOption(creationOption) {
        def url = new StringBuilder(baseUrl)
        if(creationOption) {
            url << "${urlOptionSeparator}${creationOption}"
        }
        urlOptions.each { option -> url << "${urlOptionSeparator}${option}"}
        url.toString()
    }

    private Sql internalGetSql() {
        String url = getUrlForCreationOption(urlOptionIfExists)
        try {
            return Sql.newInstance(url, user, password, driver)
        } catch (Exception e) {
            if(!shouldCreateDBOnError(e)) throw e
            log.info("Cannot connect to $url. Trying to (re)create the DB")
            create(e)
            return Sql.newInstance(url, user, password, driver)
        }
    }

    protected void create(Exception e) {
        deleteDBDirectoryIfNeeded(e)
        String url = getUrlForCreationOption(urlOptionCreate)
        Sql sql = Sql.newInstance(url, user, password, driver)
        if(creationStatements) {
            try {
                creationStatements.each { stmt -> sql.execute(stmt) }
            } finally {
                sql.close()
            }
        }
    }

    private void deleteDBDirectoryIfNeeded(Exception e) {
        File dbDir = new File(dbPath)
        if (dbDir.exists()) {
            if (shouldDeleteDBDirectoryOnError(e)) {
                dbDir.isDirectory() ? dbDir.deleteDir() : dbDir.delete()
                if (dbDir.exists()) {
                    throw new SQLException("Cannot delete existing ${dbDir.isFile() ? 'file' : 'directory'} $dbPath.")
                }
            } else {
                throw new SQLException("Cannot create a new DB: ${dbDir.isFile() ? 'file' : 'directory'} $dbPath already exists.", e)
            }
        }
    }
}
