package org.beryx.viewreka.core;

import java.util.Map;

public class Util {
	private Util() {
		throw new AssertionError("It is not allowed to instantiate the Util class");
	}

	public static <T> T requireNonNull(T value) {
		if(value == null) {
			throw new ViewrekaException("Null value not permitted");
		}
		return value;
	}

	public static <T> T requireNonNull(T value, String name) {
		if(value == null) {
			throw new ViewrekaException(name + " is null");
		}
		return value;
	}

	public static void checkNotNull(Object val, String name) {
		if(val == null) throw new ViewrekaException(name + " is null");
	}

	public static void check(boolean condition, String message) {
		if(!condition) throw new ViewrekaException(message);
	}


	@SuppressWarnings("unchecked")
	public static <T> T castOrDefault(Object value, T defaultValue, boolean allowNull) {
		if(value == null) return allowNull ? null : defaultValue;
		try {
			return (T) value;
		} catch(Exception e) {
			return defaultValue;
		}
	}

	public static <T> T getValue(Map<?,?> keyValues, Object key, T defaultValue, boolean allowNull) {
		if(!keyValues.containsKey(key)) return defaultValue;
		return castOrDefault(keyValues.get(key), defaultValue, allowNull);
	}
}
