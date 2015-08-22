package org.beryx.viewreka.core;

import java.util.Map;

/**
 * A class providing static utility methods
 */
public class Util {
    private Util() {
        throw new AssertionError("It is not allowed to instantiate the Util class");
    }


    /**
     * A method similar to {@link java.util.Objects#requireNonNull(T)}, but throwing a {@link ViewrekaException} if the specified object reference is null.
     * @param value the object reference to check for nullity
     * @param <T> the type of the reference
     * @return {@code value} if not {@code null}
     * @throws ViewrekaException if {@code value} is {@code null}
     */
    public static <T> T requireNonNull(T value) {
        if(value == null) {
            throw new ViewrekaException("Null value not permitted");
        }
        return value;
    }

    /**
     * A method similar to {@link java.util.Objects#requireNonNull(T, String)}, but throwing a customized {@link ViewrekaException} if the specified object reference is null.
     * @param value the object reference to check for nullity
     * @param name a name used to refer to the {@code value}. In the event that a {@code ViewrekaException} is thrown, this name will be used to customize its message.
     * @param <T> the type of the reference
     * @return {@code value} if not {@code null}
     * @throws ViewrekaException if {@code value} is {@code null}
     */
    public static <T> T requireNonNull(T value, String name) {
        if(value == null) {
            throw new ViewrekaException(name + " is null");
        }
        return value;
    }

    /**
     * Throws  a customized {@link ViewrekaException} if the specified object reference is null.
     * @param value the object reference to check for nullity
     * @param name a name used to refer to the {@code value}. In the event that a {@code ViewrekaException} is thrown, this name will be used to customize its message.
     * @throws ViewrekaException if {@code value} is {@code null}
     */
    public static void checkNotNull(Object value, String name) {
        if(value == null) throw new ViewrekaException(name + " is null");
    }

    /**
     * Throws  a customized {@link ViewrekaException} if the specified condition is not met.
     * @param condition the condition to be checked
     * @param message the message used in the event that a {@code ViewrekaException} is thrown.
     * @throws ViewrekaException if the {@code condition} is not met.
     */
    public static void check(boolean condition, String message) {
        if(!condition) throw new ViewrekaException(message);
    }


    /**
     * Casts a value to another type, returning a default value if the cast is not possible or if the value is null and null values are not allowed.
     * @param type the type to which the value should be cast
     * @param value the value to be cast
     * @param defaultValue the default value to be returned if the cast is not possible or if the {@code value} is null and null values are not allowed.
     * @param allowNull if false and the {@code value} is null, the {@code defaultValue} will be returned.
     * @param T the type to which the value should be cast and the type of the {@code defaultValue}.
     * @return the result of the cast or the {@code defaultValue} if the cast is not possible or if the value is null and null values are not allowed.
     */
    @SuppressWarnings("unchecked")
    public static <T> T castOrDefault(Class<T> type, Object value, T defaultValue, boolean allowNull) {
        if(value == null) return allowNull ? null : defaultValue;
        if(!type.isAssignableFrom(value.getClass())) {
            return defaultValue;
        }
        return (T) value;
    }

    /**
     * Retrieves the value with a specified key from a given map, returning a default value if the map does not contain the specified key or if the retrieved value is null and null values are not allowed.
     * @param keyValues the map from which the value should be retrieved.
     * @param defaultValue the default value to be returned if the map does not contain the specified {@code key} or if the retrieved value is null and {@code allowNull} is false.
     * @param key the key used to retrieve the value from the {@code keyValues} map.
     * @param allowNull if false and the retrieved value is null, the {@code defaultValue} will be returned.
     * @param T the type of the value to be retrieved and the type of the {@code defaultValue}.
     * @return the retrieved value or the {@code defaultValue} if the map does not contain the specified key or if the retrieved value is null and {@code allowNull} is false.
     */
    public static <T> T getValue(Map<?,?> keyValues, Object key, T defaultValue, boolean allowNull) {
        if(!keyValues.containsKey(key)) return defaultValue;
        Object value = keyValues.get(key);
        if(value == null) return allowNull ? null : defaultValue;
        return (T) value;
    }
}
