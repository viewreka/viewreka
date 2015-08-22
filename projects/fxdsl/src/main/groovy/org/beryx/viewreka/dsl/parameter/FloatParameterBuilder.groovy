package org.beryx.viewreka.dsl.parameter
import groovy.transform.InheritConstructors
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.AbstractParameter.Builder
import org.beryx.viewreka.parameter.FloatParameter
import org.beryx.viewreka.parameter.ParameterGroup
/**
 * A builder for {@link FloatParameter}s.
 */
@InheritConstructors
class FloatParameterBuilder extends ParameterBuilder<Float, FloatParameter, ParameterDelegate<Float>> {

    @Override
    public ParameterDelegate<Float> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
        return new ParameterDelegate<Float>(name, dataSetProviders);
    }

    @Override
    public Builder<Float, FloatParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
        return new FloatParameter.Builder(name, parameterGroup);
    }
}
