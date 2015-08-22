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
