package org.beryx.viewreka.dsl.parameter

import groovy.transform.InheritConstructors

import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.ParameterGroup
import org.beryx.viewreka.parameter.SqlDateParameter
import org.beryx.viewreka.parameter.AbstractParameter.Builder

/**
 * A builder for {@link SqlDateParameter}s.
 */
@InheritConstructors
class SqlDateParameterBuilder extends ParameterBuilder<java.sql.Date, SqlDateParameter, DateParameterDelegate<java.sql.Date>> {

    @Override
    public DateParameterDelegate<java.sql.Date> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
        return new DateParameterDelegate<java.sql.Date>(name, dataSetProviders);
    }

    @Override
    public Builder<java.sql.Date, SqlDateParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
        return new SqlDateParameter.Builder(name, parameterGroup);
    }
}
