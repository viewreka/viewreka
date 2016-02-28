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

import java.util.Comparator;

/**
 * The base class of parameters with comparable value types.
 * @param <T> the parameter value type
 */
public abstract class AbstractComparableValueParameter<T extends Comparable<T>> extends AbstractParameter<T> {

    public abstract static class CompBuilder<TT extends Comparable<TT>, PP extends AbstractParameter<TT>> extends AbstractParameter.Builder<TT, PP>{
        public CompBuilder(String name, Class<TT> type, ParameterGroup parameterGroup) {
            super(name, type, parameterGroup, createValueComparator());
        }

        private static final<TT extends Comparable<TT>> Comparator<TT> createValueComparator() {
            return (val1, val2) -> {
                if(val1 == null) return (val2 == null) ? 0 : -1;
                if(val2 == null) return 1;
                return val1.compareTo(val2);
            };
        }
    }

    public AbstractComparableValueParameter(CompBuilder<T, ? extends AbstractParameter<T>> builder) {
        super(builder);
    }

}
