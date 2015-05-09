package org.beryx.viewreka.dsl.chart;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import org.beryx.viewreka.dsl.transform.AliasHandler;


public interface AxisHandler extends AliasHandler<Supplier<Class<?>[]>>{

	public static class String implements AxisHandler {
		@Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return StringSuppplier.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("string"); }
	}

	public static class Integer implements AxisHandler {
		@Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return IntSupplier.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("int"); }
	}

	public static class Long implements AxisHandler {
		@Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return LongSupplier.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("long"); }
	}

	public static class Float implements AxisHandler {
		@Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return FloatSupplier.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("float"); }
	}

	public static class Double implements AxisHandler {
		@Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return DoubleSupplier.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("double"); }
	}

	public static class SqlDate implements AxisHandler {
		@Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return SqlDateSupplier.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("sqlDate"); }
	}

	public static class SqlTime implements AxisHandler {
		@Override public Class<? extends Supplier<Class<?>[]>> getAliasClass() { return SqlTimeSupplier.class; }
		@Override public Collection<java.lang.String> getAliases() { return Arrays.asList("sqlTime"); }
	}

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
