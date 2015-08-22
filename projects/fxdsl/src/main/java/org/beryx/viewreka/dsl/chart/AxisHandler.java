package org.beryx.viewreka.dsl.chart;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import org.beryx.viewreka.dsl.transform.AliasHandler;

/**
 * The {@link AliasHandler} interface used by <code>xAxis</code> or <code>yAxis</code> type aliases
 * (such as <code>string</code>, <code>int</code>, <code>double</code>, <code>sqlDate</code> etc.).
 */
public interface AxisHandler extends AliasHandler<Supplier<Class<?>[]>>{

    /** The {@link AxisHandler} of <code>string</code> axes */
    public static class String implements AxisHandler {
        @Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return StringSuppplier.class; }
        @Override public Collection<java.lang.String> getAliases() { return Arrays.asList("string"); }
    }

    /** The {@link AxisHandler} of <code></code> axes */
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
