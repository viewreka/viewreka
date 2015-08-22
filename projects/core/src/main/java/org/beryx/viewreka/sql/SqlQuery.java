package org.beryx.viewreka.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.beryx.viewreka.model.Query;
import org.beryx.viewreka.parameter.ParameterGroup;

/**
 * A {@link Query} backed by an SQL {@link PreparedStatement}.
 */
public interface SqlQuery extends Query {
    /**
     * Retrieves a prepared statement for the specified connection and parameter group.
     * @param connection the SQL {@link Connection}
     * @param parameterGroup the parameter group associated with this query
     * @return the {@link PreparedStatement}
     */
    PreparedStatement getPreparedStatement(Connection connection, ParameterGroup parameterGroup);
}
