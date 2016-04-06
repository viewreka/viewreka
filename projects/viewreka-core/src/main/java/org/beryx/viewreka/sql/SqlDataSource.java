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

import org.beryx.viewreka.model.DataSource;
import org.beryx.viewreka.parameter.ParameterGroup;

/**
 * A data source interface for SQL databases accessed via JDBC.
 */
public interface SqlDataSource extends DataSource<SqlQuery> {
    /**
     * Retrieves a provider offering an SQL {@link Connection}.
     * @return the SQL connection provider
     */
    SqlConnectionProvider getConnectionProvider();

    @Override
    default SqlDatasetProvider getDatasetProvider(String name, SqlQuery query, ParameterGroup globalParameterGroup) {
        return new SqlDatasetProvider(name, getConnectionProvider(), query, globalParameterGroup);
    }
}