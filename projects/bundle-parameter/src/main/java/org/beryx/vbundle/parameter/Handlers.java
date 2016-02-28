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
package org.beryx.vbundle.parameter;

import java.util.Arrays;
import java.util.Collection;

import org.beryx.viewreka.dsl.parameter.ParameterBuilder;
import org.beryx.viewreka.dsl.parameter.ParameterHandler;


/**
 * Container for standard {@link ParameterHandler} classes
 */
public class Handlers {

    /** The {@link Handlers} of <code>int</code> parameters */
    public static class Integer implements ParameterHandler {
        @Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return IntParameterBuilder.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("int"); }
    }

    /** The {@link Handlers} of <code>long</code> parameters */
    public static class Long implements ParameterHandler {
        @Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return LongParameterBuilder.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("long"); }
    }

    /** The {@link Handlers} of <code>float</code> parameters */
    public static class Float implements ParameterHandler {
        @Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return FloatParameterBuilder.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("float"); }
    }

    /** The {@link Handlers} of <code>double</code> parameters */
    public static class Double implements ParameterHandler {
        @Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return DoubleParameterBuilder.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("double"); }
    }

    /** The {@link Handlers} of <code>string</code> parameters */
    public static class String implements ParameterHandler {
        @Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return StringParameterBuilder.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("string"); }
    }

    /** The {@link Handlers} of <code>sqlDate</code> parameters */
    public static class SqlDate implements ParameterHandler {
        @Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return SqlDateParameterBuilder.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("sqlDate"); }
    }

    /** The {@link Handlers} of <code>sqlTime</code> parameters */
    public static class SqlTime implements ParameterHandler {
        @Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return SqlTimeParameterBuilder.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("sqlTime"); }
    }

    /** The {@link Handlers} of <code>sqlTimestamp</code> parameters */
    public static class SqlTimestamp implements ParameterHandler {
        @Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return SqlTimestampParameterBuilder.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("sqlTimestamp"); }
    }
}
