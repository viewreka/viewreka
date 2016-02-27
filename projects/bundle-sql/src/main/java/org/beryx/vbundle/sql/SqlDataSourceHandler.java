package org.beryx.vbundle.sql;

import org.beryx.vbundle.sql.SqlDataSourceBuilder;
import org.beryx.viewreka.dsl.data.DataSourceBuilder;
import org.beryx.viewreka.dsl.data.DataSourceHandler;

import java.util.Arrays;
import java.util.Collection;

/**
 * The {@link DataSourceHandler} associated with the <code>sql</code> alias.
 */
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
