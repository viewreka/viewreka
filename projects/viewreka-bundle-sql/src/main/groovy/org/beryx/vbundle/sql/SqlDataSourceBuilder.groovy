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
package org.beryx.vbundle.sql

import org.beryx.viewreka.dsl.data.DataSourceBuilder
import org.beryx.viewreka.dsl.data.QueryBuilder

import static org.beryx.viewreka.core.Util.requireNonNull
import groovy.transform.ToString

import java.sql.Driver
import java.sql.DriverManager

import org.beryx.viewreka.model.DataSource
import org.beryx.viewreka.model.Query;
import org.beryx.viewreka.sql.SqlDataSourceImpl

/**
 * A {@link DataSourceBuilder} implementation for creating {@link SqlDataSource}s.
 */
class SqlDataSourceBuilder implements DataSourceBuilder {
    final SqlQueryBuilder queryBuilder = new SqlQueryBuilder()

    @ToString(includePackage=false)
    static class ConnectionInfo {
        String driver
        String connection
        String user
        String password
    }

    @Override
    public <Q extends Query> DataSource<Q> build(String dsName, Closure dsClosure) {
        def connInfo = new ConnectionInfo()
        dsClosure.delegate = connInfo
        dsClosure.resolveStrategy = Closure.DELEGATE_FIRST
        dsClosure.call()

        String sqlDriver = requireNonNull(connInfo.driver, "driver");
        String connection = requireNonNull(connInfo.connection, "connection");

        Class<?> driverClass = Class.forName(sqlDriver, true, Thread.currentThread().getContextClassLoader());
        Driver driver = (Driver)driverClass.getConstructor().newInstance();
        DriverManager.registerDriver(new DriverDecorator(driver));

        return new SqlDataSourceImpl(dsName, connection, connInfo.user, connInfo.password);
    }

    @Override
    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

}
