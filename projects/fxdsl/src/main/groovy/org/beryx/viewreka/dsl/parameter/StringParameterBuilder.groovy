package org.beryx.viewreka.dsl.parameter

import java.util.Map;

import groovy.transform.InheritConstructors;

import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.AbstractParameter.Builder;
import org.beryx.viewreka.parameter.StringParameter;
import org.beryx.viewreka.parameter.ParameterGroup;

@InheritConstructors
class StringParameterBuilder extends ParameterBuilder<String, org.beryx.viewreka.parameter.StringParameter, ParameterDelegate<String>> {

	@Override
	public ParameterDelegate<String> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
		return new ParameterDelegate<String>(name, dataSetProviders);
	}

	@Override
	public Builder<String, StringParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
		return new StringParameter.Builder(name, parameterGroup);
	}
}
