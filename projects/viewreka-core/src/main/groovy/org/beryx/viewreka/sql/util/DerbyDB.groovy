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
class DerbyDB {
    private static final DRIVER = 'org.apache.derby.jdbc.EmbeddedDriver'

    final String dbPath
    final String url
    final String user
    final String password

    boolean createIfNeeded
    boolean deleteExistingOnCreate
    List<String> creationStatements

    DerbyDB(String dbPath, String user, String password) {
        this.dbPath = dbPath
        this.url = "jdbc:derby:$dbPath"
        this.user = user
        this.password = password
    }

    DerbyDB withCreationStrategy(boolean createIfNeeded, boolean deleteExistingOnCreate = false) {
        this.createIfNeeded = createIfNeeded
        this.deleteExistingOnCreate = deleteExistingOnCreate
        this
    }

    DerbyDB withCreationStatementList(List<String> creationStatements) {
        this.creationStatements = creationStatements
        this
    }

    DerbyDB withCreationStatements(String creationStatements) {
        this.creationStatements = splitStatements(creationStatements)
        this
    }

    DerbyDB withCreationStatementFile(File creationStatementsFile){
        this.creationStatements = splitStatements(creationStatementsFile.text)
        this
    }

    DerbyDB withCreationStatementFile(String creationStatementsFile){
        this.creationStatements = splitStatements(new File(creationStatementsFile).text)
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

    private Sql internalGetSql() {
        try {
            return Sql.newInstance(url, user, password, DRIVER)
        } catch (Exception e) {
            if(!createIfNeeded) throw e
            log.info("Cannot connect to $url. Trying to (re)create the DB")
            create()
            return Sql.newInstance(url, user, password, DRIVER)
        }
    }

    void create() {
        File dbDir = new File(dbPath)
        if(dbDir.exists()) {
            if(deleteExistingOnCreate) {
                dbDir.isDirectory() ? dbDir.deleteDir() : dbDir.delete()
                if(dbDir.exists()) {
                    throw new SQLException("Cannot delete existing ${dbDir.isFile() ? 'file' : 'directory'} $dbPath.")
                }
            } else {
                throw new SQLException("${dbDir.isFile() ? 'File' : 'Directory'} $dbPath already exists.")
            }
        }
        Sql sql = Sql.newInstance("$url;create=true", user, password, DRIVER)
        if(creationStatements) {
            try {
                creationStatements.each { stmt ->
                    sql.execute(stmt)
                }
            } finally {
                sql.close()
            }
        }
    }
}
