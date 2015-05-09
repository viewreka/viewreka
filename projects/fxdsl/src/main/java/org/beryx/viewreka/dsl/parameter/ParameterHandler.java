package org.beryx.viewreka.dsl.parameter;

import java.util.Arrays;
import java.util.Collection;

import org.beryx.viewreka.dsl.parameter.DoubleParameterBuilder;
import org.beryx.viewreka.dsl.parameter.IntParameterBuilder;
import org.beryx.viewreka.dsl.parameter.LongParameterBuilder;
import org.beryx.viewreka.dsl.parameter.ParameterBuilder;
import org.beryx.viewreka.dsl.parameter.SqlDateParameterBuilder;
import org.beryx.viewreka.dsl.parameter.SqlTimeParameterBuilder;
import org.beryx.viewreka.dsl.parameter.SqlTimestampParameterBuilder;
import org.beryx.viewreka.dsl.parameter.StringParameterBuilder;
import org.beryx.viewreka.dsl.transform.AliasHandler;


public interface ParameterHandler extends AliasHandler<ParameterBuilder<?,?,?>> {
	public static class Integer implements ParameterHandler {
		@Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return IntParameterBuilder.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("int"); }
	}

	public static class Long implements ParameterHandler {
		@Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return LongParameterBuilder.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("long"); }
	}

	public static class Float implements ParameterHandler {
		@Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return FloatParameterBuilder.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("float"); }
	}

	public static class Double implements ParameterHandler {
		@Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return DoubleParameterBuilder.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("double"); }
	}

	public static class String implements ParameterHandler {
		@Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return StringParameterBuilder.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("string"); }
	}

	public static class SqlDate implements ParameterHandler {
		@Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return SqlDateParameterBuilder.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("sqlDate"); }
	}

	public static class SqlTime implements ParameterHandler {
		@Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return SqlTimeParameterBuilder.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("sqlTime"); }
	}

	public static class SqlTimestamp implements ParameterHandler {
		@Override public Class<? extends ParameterBuilder<?, ?, ?>> getAliasClass() { return SqlTimestampParameterBuilder.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("sqlTimestamp"); }
	}
}
