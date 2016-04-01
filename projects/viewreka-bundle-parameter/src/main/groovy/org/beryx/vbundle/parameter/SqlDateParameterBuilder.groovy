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
package org.beryx.vbundle.parameter

import groovy.transform.InheritConstructors
import org.beryx.viewreka.dsl.parameter.ParameterBuilder
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.ParameterGroup
import org.beryx.viewreka.parameter.SqlDateParameter
import org.beryx.viewreka.parameter.AbstractParameter.Builder

import java.sql.Date

/**
 * A builder for {@link SqlDateParameter}s.
 */
@InheritConstructors
class SqlDateParameterBuilder extends ParameterBuilder<Date, SqlDateParameter, DateParameterDelegate<java.sql.Date>> {

    @Override
    public DateParameterDelegate<java.sql.Date> createDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
        return new DateParameterDelegate<java.sql.Date>(name, dataSetProviders);
    }

    @Override
    public Builder<java.sql.Date, SqlDateParameter> createCoreBuilder(String name, ParameterGroup parameterGroup) {
        return new SqlDateParameter.Builder(name, parameterGroup);
    }
}
