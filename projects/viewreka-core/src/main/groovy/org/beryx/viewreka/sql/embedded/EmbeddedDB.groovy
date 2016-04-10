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

import groovy.sql.Sql
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.SimpleType
import groovy.util.logging.Slf4j

import java.sql.ResultSet
import java.sql.SQLException

/**
 * A facade that simplifies the usage of embedded databases.
 * <br>It allows defining strategies for automatically creating and initializing the database if it does not exist
 * or for deleting it in some user-defined error situations (for example, if the database is corrupted).
 */
@Slf4j
abstract class EmbeddedDB {
    final String driverClassName
    final String dbPathAndName
    final String user
    final String password

    final List<String> urlOptions = []
    final List<String> creationStatements = []

    final List<String> sqlStatesForCreateDB = []
    final List<String> sqlStatesForDeleteDB = []

    String urlOptionSeparator = ';'

    /** The basic connection URL, which includes no options */
    abstract String getBaseUrl()

    /** The URL option for creating the database, if it does not exist */
    abstract String getUrlOptionCreate()

    /** The URL option for connecting to the database only if it exists */
    abstract String getUrlOptionIfExists()

    abstract void deleteDB()

    EmbeddedDB(String driverClassName, String dbPathAndName, String user, String password) {
        this.driverClassName = driverClassName
        this.dbPathAndName = dbPathAndName
        this.user = user
        this.password = password
    }


    /**
     * Sets an option that will be appended to the database URL used to create a connection.
     * <br>May be called multiple time in order to configure several options.
     */
    final EmbeddedDB withUrlOption(String option) {
        urlOptions << option
        this
    }

    /**
     * Configures the error states returned by {@link java.sql.SQLException#getSQLState()} that will trigger the automatic creation and initialization of the database.
     * <br>Typically, the argument passed to this method is the error state informing that the database does not exist.
     */
    final EmbeddedDB withSqlStatesForCreateDB(String... states) {
        this.sqlStatesForCreateDB.addAll(states)
        this
    }

    /**
     * Configures the error states returned by {@link java.sql.SQLException#getSQLState()} that will trigger the automatic deletion of the database.
     * <br>Typically, the arguments passed to this method are error states informing that the database is corrupted.
     */
    final EmbeddedDB withSqlStatesForDeleteDB(String... states) {
        this.sqlStatesForDeleteDB.addAll(states)
        this
    }

    /**
     * Configures a (typically DDL or INSERT) statement to be executed after creating the database.
     * <br>May be called multiple time in order to configure several statements, which will be executed in the order in which they have been configured.
     */
    final EmbeddedDB withCreationStatement(String creationStatement) {
        this.creationStatements << creationStatement
        this
    }

    /**
     * Configures a list of (typically DDL or INSERT) statements to be executed after creating the database.
     */
    final EmbeddedDB withCreationStatementList(List<String> creationStatements) {
        this.creationStatements.addAll(creationStatements)
        this
    }

    /**
     * Configures a script containing a number of (typically DDL or INSERT) statements to be executed after creating the database.
     * <br>The statements should be separated by lines containing only the '/' character.
     */
    final EmbeddedDB withCreationStatements(String creationStatements) {
        this.creationStatements.addAll(splitStatements(creationStatements))
        this
    }

    /**
     * Configures a file containing a number of (typically DDL or INSERT) statements to be executed after creating the database.
     * <br>The statements should be separated by lines containing only the '/' character.
     */
    final EmbeddedDB withCreationStatementFile(File creationStatementsFile){
        this.creationStatements.addAll(splitStatements(creationStatementsFile.text))
        this
    }

    /**
     * Configures the name of a file containing a number of (typically DDL or INSERT) statements to be executed after creating the database.
     * <br>The statements should be separated by lines containing only the '/' character.
     */
    final EmbeddedDB withCreationStatementFile(String creationStatementsFile){
        this.creationStatements.addAll(splitStatements(new File(creationStatementsFile).text))
        this
    }

    private static List<String> splitStatements(String sqlStatements) {
        List<String> statements = []
        def splitted = sqlStatements?.normalize()?.split('\\n\\s*/\\s*\\n')
        splitted?.each { stmt ->
            stmt = stmt.trim()
            if (stmt.endsWith(';')) stmt = stmt.substring(0, stmt.length() - 1)
            if (stmt) statements << stmt
        }
        statements
    }

    /**
     * Executes a closure that takes an argument of type {@link Sql}.
     * <br>This method may delete an existing database and/or create a new one in accordance with the defined strategies.
     */
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

    /**
     * Executes one or more statements.
     * The statements should be separated by lines containing only the '/' character.
     * <br>This method may delete an existing database and/or create a new one in accordance with the defined strategies.
     */
    def executeStatements(String statements) {
        execute {Sql sql -> splitStatements(statements).each {stmt -> sql.execute(stmt)}}
    }

    /**
     * Executes a given SQL statement and (optionally) processes the results.
     * <br>This method may delete an existing database and/or create a new one in accordance with the defined strategies.
     */
    def executeStatement(String statement, Closure processResults = null) {
        execute {Sql sql -> processResults ? sql.execute(statement, processResults) : sql.execute(statement)}
    }

    /**
     * Executes a given SQL statement with parameters and (optionally) processes the results.
     * <br>This method may delete an existing database and/or create a new one in accordance with the defined strategies.
     */
    def executeStatement(String query, List params, Closure closure = null) {
        execute {Sql sql ->
            closure ? sql.execute(query, params, closure) : sql.execute(query, params)
        }
    }

    /**
     * Executes a given query and processes the result set.
     * <br>This method may delete an existing database and/or create a new one in accordance with the defined strategies.
     */
    def executeSelect(String query, @ClosureParams(value=SimpleType, options="java.sql.ResultSet")  Closure processResultSet) {
        execute {Sql sql -> sql.query(query, { ResultSet rs -> processResultSet.call(rs)})}
    }

    /**
     * Executes a given query with parameters and processes the result set.
     * <br>This method may delete an existing database and/or create a new one in accordance with the defined strategies.
     */
    def executeSelect(String query, List params, @ClosureParams(value=SimpleType, options="java.sql.ResultSet") Closure processResultSet) {
        execute {Sql sql -> sql.query(query, params, { ResultSet rs -> processResultSet.call(rs)})}
    }

    /**
     * Defines the strategy for creating a new database. Returns true, if a database should be created if the exception passed as argument occurred.
     * <br>This implementation uses the SQL states configured using {@link #withSqlStatesForCreateDB(java.lang.String[])}
     * to decide whether a new database should be created.
     * <br>Subclasses may override this method in order to provide another strategy.
     */
    protected boolean shouldCreateDBOnError(Exception e) {
        if(e instanceof SQLException) {
            String sqlState = ((SQLException)e).getSQLState()
            return sqlStatesForCreateDB.contains(sqlState)
        }
        return false
    }

    /**
     * Defines the strategy for deleting a database. Returns true, if the database should be deleted if the exception passed as argument occurred.
     * <br>This implementation uses the SQL states configured using {@link #withSqlStatesForDeleteDB(java.lang.String[])}
     * to decide whether the database should be deleted.
     * <br>Subclasses may override this method in order to provide another strategy.
     */
    protected boolean shouldDeleteDBOnError(Exception e) {
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
            return Sql.newInstance(url, user, password, this.driverClassName)
        } catch (Exception e) {
            if (shouldDeleteDBOnError(e)) {
                deleteDB()
            }
            if(!shouldCreateDBOnError(e)) throw e
            createDB()
            return Sql.newInstance(url, user, password, this.driverClassName)
        }
    }

    protected void createDB() {
        log.info("Creating the DB $dbPathAndName...")
        String url = getUrlForCreationOption(urlOptionCreate)
        Sql sql = Sql.newInstance(url, user, password, this.driverClassName)
        if(creationStatements) {
            try {
                creationStatements.each { stmt -> sql.execute(stmt) }
            } finally {
                sql.close()
            }
        }
    }
}
