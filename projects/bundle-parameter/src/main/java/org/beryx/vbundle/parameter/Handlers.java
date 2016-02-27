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
