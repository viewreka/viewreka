/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.viewreka.dsl.parameter
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.AbstractParameter
import org.beryx.viewreka.parameter.ParameterGroup
/**
 * An abstract parameter builder.
 */
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
