package org.beryx.viewreka.dsl.parameter

import java.util.Map;

import groovy.transform.InheritConstructors;

import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.AbstractParameter.Builder;
import org.beryx.viewreka.parameter.DoubleParameter;
import org.beryx.viewreka.parameter.ParameterGroup;

@InheritConstructors
class DoubleParameterBuilder extends ParameterBuilder<Double, org.beryx.viewreka.parameter.DoubleParameter, ParameterDelegate<Double>> {

	@Override
	public ParameterDelegate<Double> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
		return new ParameterDelegate<Double>(name, dataSetProviders);
	}

	@Override
	public Builder<Double, DoubleParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
		return new DoubleParameter.Builder(name, parameterGroup);
	}
}
