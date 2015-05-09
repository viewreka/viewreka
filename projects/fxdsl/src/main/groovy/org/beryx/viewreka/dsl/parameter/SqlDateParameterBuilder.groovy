package org.beryx.viewreka.dsl.parameter

import java.util.Map;

import groovy.transform.InheritConstructors;

import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.AbstractParameter.Builder;
import org.beryx.viewreka.parameter.ParameterGroup;

@InheritConstructors
class SqlDateParameterBuilder extends ParameterBuilder<java.sql.Date, org.beryx.viewreka.parameter.SqlDateParameter, DateParameterDelegate<java.sql.Date>> {

	@Override
	public DateParameterDelegate<java.sql.Date> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
		return new DateParameterDelegate<java.sql.Date>(name, dataSetProviders);
	}

	@Override
	public Builder<java.sql.Date, org.beryx.viewreka.parameter.SqlDateParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
		return new org.beryx.viewreka.parameter.SqlDateParameter.Builder(name, parameterGroup);
	}
}
