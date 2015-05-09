package org.beryx.viewreka.dsl.parameter

import java.util.Map;

import org.beryx.viewreka.fxui.FxView
import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.AbstractParameter;
import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.parameter.ParameterGroup;

abstract class ParameterBuilder<T, P extends AbstractParameter<T>, D extends ParameterDelegate<T>> {

	abstract D createDelegate(String name, Map<String, DatasetProvider> dataSetProviders)
	abstract AbstractParameter.Builder<T, P> createCoreBuilder(String name, ParameterGroup parameterGroup)

	public P build(String name, Closure closure, ParameterGroup parameterGroup, Map<String, DatasetProvider> dataSetProviders) {
		def delegate = createDelegate(name, dataSetProviders)
		if(closure) {
			closure.delegate = delegate
			closure.resolveStrategy = Closure.DELEGATE_FIRST
			closure.call()
		}
		AbstractParameter.Builder<T, P> coreBuilder = createCoreBuilder(name, parameterGroup)
		configureCoreBuilder(delegate, coreBuilder)
		return coreBuilder.build()
	}

	protected configureCoreBuilder(D delegate, AbstractParameter.Builder<T, P> coreBuilder) {
		coreBuilder
			.datasetProvider(delegate.datasetProvider)
			.maxValue(delegate.maxValue)
			.maxValueAllowed(delegate.maxValueAllowed)
			.minValue(delegate.minValue)
			.minValueAllowed(delegate.minValueAllowed)
			.nullAllowed(delegate.nullAllowed)
			.valColumn(delegate.valColumn)
			.valColumnName(delegate.valColumnName)
			.textValColumn(delegate.textValColumn)
			.textValColumnName(delegate.textValColumnName)
			.displayedValColumn(delegate.displayedValColumn)
			.displayedValColumnName(delegate.displayedValColumnName)
	}
}
