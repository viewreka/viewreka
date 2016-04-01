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


/**
 * The default implementation of {@link SqlDataSource}.
 */
public class SqlDataSourceImpl implements SqlDataSource {
    private final String name;
    private final SqlConnectionProvider connectionProvider;

    /**
     * Constructs an SQL data source with the specified name and connection parameters.
     * @param name the data source name
     * @param url a database url of the form jdbc:subprotocol:subname
     * @param user the database user on whose behalf the connection is being made
     * @param password the user's password
     */
    public SqlDataSourceImpl(String name, String url, String user, String password) {
        this.name = name;
        this.connectionProvider = new SqlConnectionProviderImpl(url, user, password);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SqlConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

    @Override
    public void close() throws Exception {
        connectionProvider.close();
    }
}
