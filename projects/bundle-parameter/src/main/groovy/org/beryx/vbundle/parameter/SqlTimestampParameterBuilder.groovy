package org.beryx.vbundle.parameter

import groovy.transform.InheritConstructors
import org.beryx.viewreka.dsl.parameter.ParameterBuilder
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.ParameterGroup
import org.beryx.viewreka.parameter.SqlTimestampParameter
import org.beryx.viewreka.parameter.AbstractParameter.Builder

import java.sql.Timestamp

/**
 * A builder for {@link SqlTimestampParameter}s.
 */
@InheritConstructors
class SqlTimestampParameterBuilder extends ParameterBuilder<Timestamp, SqlTimestampParameter, DateParameterDelegate<java.sql.Timestamp>> {

    @Override
    public DateParameterDelegate<java.sql.Timestamp> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
        return new DateParameterDelegate<java.sql.Timestamp>(name, dataSetProviders);
    }

    @Override
    public Builder<java.sql.Timestamp, SqlTimestampParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
        return new SqlTimestampParameter.Builder(name, parameterGroup);
    }
}
