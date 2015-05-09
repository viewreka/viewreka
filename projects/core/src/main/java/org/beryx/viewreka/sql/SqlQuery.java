package org.beryx.viewreka.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.beryx.viewreka.model.Query;
import org.beryx.viewreka.parameter.ParameterGroup;

public interface SqlQuery extends Query {
	PreparedStatement getPreparedStatement(Connection connection, ParameterGroup parameterGroup);
}
