package org.beryx.viewreka.dsl.parameter

import java.util.Map;

import groovy.transform.InheritConstructors;

import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.AbstractParameter.Builder;
import org.beryx.viewreka.parameter.FloatParameter;
import org.beryx.viewreka.parameter.ParameterGroup;

@InheritConstructors
class FloatParameterBuilder extends ParameterBuilder<Float, org.beryx.viewreka.parameter.FloatParameter, ParameterDelegate<Float>> {

	@Override
	public ParameterDelegate<Float> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
		return new ParameterDelegate<Float>(name, dataSetProviders);
	}

	@Override
	public Builder<Float, FloatParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
		return new FloatParameter.Builder(name, parameterGroup);
	}
}
