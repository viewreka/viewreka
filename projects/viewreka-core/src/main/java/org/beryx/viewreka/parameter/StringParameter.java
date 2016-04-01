/**
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
package org.beryx.viewreka.parameter;


/**
 * A parameter with values of type String.
 */
public class StringParameter extends AbstractComparableValueParameter<String> {

    public static class Builder extends AbstractComparableValueParameter.CompBuilder<String, StringParameter> {
        public Builder(String name, ParameterGroup parameterGroup) {
            super(name, String.class, parameterGroup);
        }

        @Override
        public StringParameter build() {
            return new StringParameter(this);
        }
    }

    private StringParameter(Builder builder) {
        super(builder);
    }

    @Override
    public Class<String> getValueClass() {
        return String.class;
    }

    @Override
    public String toValue(String sVal) throws Exception {
        return sVal;
    }

}
