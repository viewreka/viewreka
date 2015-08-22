package org.beryx.viewreka.dsl.parameter

import groovy.transform.InheritConstructors

import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.ParameterGroup
import org.beryx.viewreka.parameter.SqlTimeParameter
import org.beryx.viewreka.parameter.AbstractParameter.Builder

/**
 * A builder for {@link SqlTimeParameter}s.
 */
@InheritConstructors
class SqlTimeParameterBuilder extends ParameterBuilder<java.sql.Time, SqlTimeParameter, DateParameterDelegate<java.sql.Time>> {

    @Override
    public DateParameterDelegate<java.sql.Time> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
        return new DateParameterDelegate<java.sql.Time>(name, dataSetProviders);
    }

    @Override
    public Builder<java.sql.Time, SqlTimeParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
        return new SqlTimeParameter.Builder(name, parameterGroup);
    }
}
