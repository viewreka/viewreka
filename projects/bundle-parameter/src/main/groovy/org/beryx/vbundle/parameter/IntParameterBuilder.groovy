package org.beryx.vbundle.parameter
import groovy.transform.InheritConstructors
import org.beryx.viewreka.dsl.parameter.ParameterBuilder
import org.beryx.viewreka.dsl.parameter.ParameterDelegate
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.AbstractParameter.Builder
import org.beryx.viewreka.parameter.IntParameter
import org.beryx.viewreka.parameter.ParameterGroup
/**
 * A builder for {@link IntParameter}s.
 */
@InheritConstructors
class IntParameterBuilder extends ParameterBuilder<Integer, IntParameter, ParameterDelegate<Integer>> {

    @Override
    public ParameterDelegate<Integer> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
        return new ParameterDelegate<Integer>(name, dataSetProviders);
    }

    @Override
    public Builder<Integer, IntParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
        return new IntParameter.Builder(name, parameterGroup);
    }
}
