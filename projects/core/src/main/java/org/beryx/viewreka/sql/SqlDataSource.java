package org.beryx.viewreka.sql;

import org.beryx.viewreka.model.DataSource;
import org.beryx.viewreka.parameter.ParameterGroup;

public interface SqlDataSource extends DataSource<SqlQuery> {
	SqlConnectionProvider getConnectionProvider();

	@Override
	default public SqlDatasetProvider getDatasetProvider(String name, SqlQuery query, ParameterGroup globalParameterGroup) {
		return new SqlDatasetProvider(name, getConnectionProvider(), query, globalParameterGroup);
	}
}
