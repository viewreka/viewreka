package org.beryx.viewreka.dsl.parameter

import java.util.Map;

import groovy.transform.InheritConstructors;

import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.AbstractParameter.Builder;
import org.beryx.viewreka.parameter.ParameterGroup;

@InheritConstructors
class SqlTimeParameterBuilder extends ParameterBuilder<java.sql.Time, org.beryx.viewreka.parameter.SqlTimeParameter, DateParameterDelegate<java.sql.Time>> {

	@Override
	public DateParameterDelegate<java.sql.Time> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
		return new DateParameterDelegate<java.sql.Time>(name, dataSetProviders);
	}

	@Override
	public Builder<java.sql.Time, org.beryx.viewreka.parameter.SqlTimeParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
		return new org.beryx.viewreka.parameter.SqlTimeParameter.Builder(name, parameterGroup);
	}
}
