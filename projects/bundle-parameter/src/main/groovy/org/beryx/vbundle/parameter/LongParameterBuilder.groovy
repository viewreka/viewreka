package org.beryx.vbundle.parameter
import groovy.transform.InheritConstructors
import org.beryx.viewreka.dsl.parameter.ParameterBuilder
import org.beryx.viewreka.dsl.parameter.ParameterDelegate
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.AbstractParameter.Builder
import org.beryx.viewreka.parameter.LongParameter
import org.beryx.viewreka.parameter.ParameterGroup
/**
 * A builder for {@link LongParameter}s.
 */
@InheritConstructors
class LongParameterBuilder extends ParameterBuilder<Long, LongParameter, ParameterDelegate<Long>> {

    @Override
    public ParameterDelegate<Long> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
        return new ParameterDelegate<Long>(name, dataSetProviders);
    }

    @Override
    public Builder<Long, LongParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
        return new LongParameter.Builder(name, parameterGroup);
    }
}
