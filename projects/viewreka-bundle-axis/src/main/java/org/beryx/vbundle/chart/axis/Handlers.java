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
package org.beryx.vbundle.chart.axis;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import org.beryx.viewreka.dsl.chart.AxisHandler;

/**
 * Container for standard {@link AxisHandler} classes
 */
public class Handlers {

    /** The {@link AxisHandler} of <code>string</code> axes */
    public static class String implements AxisHandler {
        @Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return StringSuppplier.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("string"); }
    }

    /** The {@link AxisHandler} of <code>int</code> axes */
    public static class Integer implements AxisHandler {
        @Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return IntSupplier.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("int"); }
    }

    /** The {@link AxisHandler} of <code>long</code> axes */
    public static class Long implements AxisHandler {
        @Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return LongSupplier.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("long"); }
    }

    /** The {@link AxisHandler} of <code>float</code> axes */
    public static class Float implements AxisHandler {
        @Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return FloatSupplier.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("float"); }
    }

    /** The {@link AxisHandler} of <code>double</code> axes */
    public static class Double implements AxisHandler {
        @Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return DoubleSupplier.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("double"); }
    }

    /** The {@link AxisHandler} of <code>sqlDate</code> axes */
    public static class SqlDate implements AxisHandler {
        @Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return SqlDateSupplier.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("sqlDate"); }
    }

    /** The {@link AxisHandler} of <code>sqlTime</code> axes */
    public static class SqlTime implements AxisHandler {
        @Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return SqlTimeSupplier.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("sqlTime"); }
    }

    /** The {@link AxisHandler} of <code>sqlTimestamp</code> axes */
    public static class SqlTimestamp implements AxisHandler {
        @Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return SqlTimestampSupplier.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("sqlTimestamp"); }
    }




    public static class StringSuppplier implements Supplier<Class<?>[]> {
        @Override public Class<?>[] get() { return new Class<?>[] {java.lang.String.class, java.lang.String.class}; }
    }
    public static class IntSupplier implements Supplier<Class<?>[]> {
        @Override public Class<?>[] get() { return new Class<?>[] {java.lang.Integer.class, java.lang.Integer.class}; }
    }
    public static class LongSupplier implements Supplier<Class<?>[]> {
        @Override public Class<?>[] get() { return new Class<?>[] {java.lang.Long.class, java.lang.Long.class}; }
    }
    public static class FloatSupplier implements Supplier<Class<?>[]> {
        @Override public Class<?>[] get() { return new Class<?>[] {java.lang.Float.class, java.lang.Float.class}; }
    }
    public static class DoubleSupplier implements Supplier<Class<?>[]> {
        @Override public Class<?>[] get() { return new Class<?>[] {java.lang.Double.class, java.lang.Double.class}; }
    }
    public static class SqlDateSupplier implements Supplier<Class<?>[]> {
        @Override public Class<?>[] get() { return new Class<?>[] {java.sql.Date.class, java.lang.Long.class}; }
    }
    public static class SqlTimeSupplier implements Supplier<Class<?>[]> {
        @Override public Class<?>[] get() { return new Class<?>[] {java.sql.Time.class, java.lang.Long.class}; }
    }
    public static class SqlTimestampSupplier implements Supplier<Class<?>[]> {
        @Override public Class<?>[] get() { return new Class<?>[] {java.sql.Timestamp.class, java.lang.Long.class}; }
    }
}
