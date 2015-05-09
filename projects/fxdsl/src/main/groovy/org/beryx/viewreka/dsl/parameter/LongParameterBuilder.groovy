package org.beryx.viewreka.dsl.parameter

import java.util.Map;

import groovy.transform.InheritConstructors;

import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.AbstractParameter.Builder;
import org.beryx.viewreka.parameter.LongParameter;
import org.beryx.viewreka.parameter.ParameterGroup;

@InheritConstructors
class LongParameterBuilder extends ParameterBuilder<Long, org.beryx.viewreka.parameter.LongParameter, ParameterDelegate<Long>> {

	@Override
	public ParameterDelegate<Long> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
		return new ParameterDelegate<Long>(name, dataSetProviders);
	}

	@Override
	public Builder<Long, LongParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
		return new LongParameter.Builder(name, parameterGroup);
	}
}
