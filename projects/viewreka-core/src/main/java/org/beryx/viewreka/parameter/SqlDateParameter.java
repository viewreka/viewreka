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

import java.sql.Date;

/**
 * A parameter with values of type java.sql.Date.
 */
public class SqlDateParameter extends AbstractDateParameter<Date> {

    public static class Builder extends DateBuilder<Date, SqlDateParameter> {
        public Builder(String name, ParameterGroup parameterGroup) {
            super(name, Date.class, parameterGroup);
        }

        @Override
        public SqlDateParameter build() {
            return new SqlDateParameter(this);
        }
    }

    private SqlDateParameter(Builder builder) {
        super(builder);
    }

    @Override
    public Class<Date> getValueClass() {
        return Date.class;
    }

    @Override
    public Date fromMilliseconds(long millis) {
        return new Date(millis);
    }
}
