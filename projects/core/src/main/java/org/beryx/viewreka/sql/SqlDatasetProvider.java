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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.ParameterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A provider that offers an {@link SqlDataset}.
 */
public class SqlDatasetProvider implements DatasetProvider {
    private static final Logger log = LoggerFactory.getLogger(SqlDatasetProvider.class);

    private final String name;
    private final SqlConnectionProvider connectionProvider;
    private final SqlQuery query;
    private final ParameterGroup globalParameterGroup;

    private boolean dirty = true;
    private SqlDataset cachedDataset = null;
    private final List<Consumer<DatasetProvider>> dirtyListeners = new ArrayList<>();

    /**
     * Constructs an SQL dataset provider
     * @param name the name of this dataset provider
     * @param connectionProvider the provider used to create an SQL {@link Connection}
     * @param query the {@link SqlQuery} used for creating a dataset
     * @param globalParameterGroup the parameter group containing the query parameters
     */
    public SqlDatasetProvider(String name, SqlConnectionProvider connectionProvider, SqlQuery query, ParameterGroup globalParameterGroup) {
        this.name = name;
        this.connectionProvider = connectionProvider;
        this.query = query;
        this.globalParameterGroup = globalParameterGroup;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getParameterNames() {
        return query.getParameterNames();
    }

    @Override
    public synchronized void setDirty() {
        log.trace("Setting dirty: {} {}", name, query);
        this.dirty = true;
        dirtyListeners.forEach(listener -> listener.accept(this));
    }

    @SuppressWarnings("resource")
    @Override
    public synchronized SqlDataset getDataset() {
        if(dirty || (cachedDataset == null) || cachedDataset.isClosed()) {
            if(cachedDataset != null) {
                cachedDataset.close();
            }
            try {
                Connection connection = connectionProvider.getConnection();
                PreparedStatement statement = query.getPreparedStatement(connection, globalParameterGroup);
                ResultSet rs = statement.executeQuery();
                cachedDataset = new SqlDataset(name, rs, query.getParameterNames(), statement);
                dirty = false;
            } catch (SQLException e) {
                throw new ViewrekaException("Failed to retrieve the dataset for: " + query, e);
            }
        }
        return cachedDataset;
    }

    @Override
    public boolean addDirtyListener(Consumer<DatasetProvider> listener) {
        return dirtyListeners.add(listener);
    }

    @Override
    public boolean removeDirtyListener(Consumer<DatasetProvider> listener) {
        return dirtyListeners.remove(listener);
    }

    @Override
    public void close() throws Exception {
        connectionProvider.close();
    }

    @Override
    public String toString() {
        return name + ": " + getParameterNames();
    }
}
