package org.beryx.viewreka.dsl.data;

import java.util.Arrays;
import java.util.Collection;

public class SqlDataSourceHandler implements DataSourceHandler {
	@Override
	public Class<? extends DataSourceBuilder> getAliasClass() {
		return SqlDataSourceBuilder.class;
	}

	@Override
	public Collection<String> getAliases() {
		return Arrays.asList("sql", "SQL", "Sql");
	}
}
