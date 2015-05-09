package org.beryx.viewreka.dsl.parameter

import java.util.Map;

import groovy.transform.InheritConstructors;

import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.AbstractParameter.Builder;
import org.beryx.viewreka.parameter.ParameterGroup;

@InheritConstructors
class SqlTimestampParameterBuilder extends ParameterBuilder<java.sql.Timestamp, org.beryx.viewreka.parameter.SqlTimestampParameter, DateParameterDelegate<java.sql.Timestamp>> {

	@Override
	public DateParameterDelegate<java.sql.Timestamp> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
		return new DateParameterDelegate<java.sql.Timestamp>(name, dataSetProviders);
	}

	@Override
	public Builder<java.sql.Timestamp, org.beryx.viewreka.parameter.SqlTimestampParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
		return new org.beryx.viewreka.parameter.SqlTimestampParameter.Builder(name, parameterGroup);
	}
}
