package org.beryx.vbundle.parameter

import groovy.transform.InheritConstructors
import org.beryx.viewreka.dsl.parameter.ParameterBuilder
import org.beryx.viewreka.dsl.parameter.ParameterDelegate
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.ParameterGroup
import org.beryx.viewreka.parameter.StringParameter
import org.beryx.viewreka.parameter.AbstractParameter.Builder

/**
 * A builder for {@link StringParameter}s.
 */
@InheritConstructors
class StringParameterBuilder extends ParameterBuilder<String, StringParameter, ParameterDelegate<String>> {

    @Override
    public ParameterDelegate<String> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
        return new ParameterDelegate<String>(name, dataSetProviders);
    }

    @Override
    public Builder<String, StringParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
        return new StringParameter.Builder(name, parameterGroup);
    }
}
