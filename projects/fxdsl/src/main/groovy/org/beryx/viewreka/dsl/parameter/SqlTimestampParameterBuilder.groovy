package org.beryx.viewreka.dsl.parameter

import groovy.transform.InheritConstructors

import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.ParameterGroup
import org.beryx.viewreka.parameter.SqlTimestampParameter
import org.beryx.viewreka.parameter.AbstractParameter.Builder

/**
 * A builder for {@link SqlTimestampParameter}s.
 */
@InheritConstructors
class SqlTimestampParameterBuilder extends ParameterBuilder<java.sql.Timestamp, SqlTimestampParameter, DateParameterDelegate<java.sql.Timestamp>> {

    @Override
    public DateParameterDelegate<java.sql.Timestamp> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
        return new DateParameterDelegate<java.sql.Timestamp>(name, dataSetProviders);
    }

    @Override
    public Builder<java.sql.Timestamp, SqlTimestampParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
        return new SqlTimestampParameter.Builder(name, parameterGroup);
    }
}
