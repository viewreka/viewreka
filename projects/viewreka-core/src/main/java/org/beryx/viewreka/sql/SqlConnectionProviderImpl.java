/**
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
package org.beryx.viewreka.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.beryx.viewreka.core.ViewrekaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of {@link SqlConnectionProvider}.
 */
public class SqlConnectionProviderImpl implements SqlConnectionProvider {
    private static final Logger log = LoggerFactory.getLogger(SqlConnectionProviderImpl.class);

    private final String url;
    private final String user;
    private final String password;

    private Connection connection = null;

    /**
     * Constructs an SQL connection provider.
     * @param url a database url of the form jdbc:subprotocol:subname
     * @param user the database user on whose behalf the connection is being made
     * @param password the user's password
     */
    public SqlConnectionProviderImpl(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Connection getConnection() {
        try {
            if((connection != null) && !connection.isValid(0)) {
                try {
                    connection.close();
                } catch (Exception e) {
                    log.warn("Error closing connection " + url, e);
                }
                connection = null;
            }
            if(connection == null) {
                connection = DriverManager.getConnection(url, user, password);
            }
            return connection;
        } catch (SQLException e) {
            throw new ViewrekaException("getConnection() failed", e);
        }
    }

    @Override
    public void close() throws SQLException {
        if(connection != null) {
            connection.close();
        }
    }
}
