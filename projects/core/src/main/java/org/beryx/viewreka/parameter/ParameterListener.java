package org.beryx.viewreka.parameter;

/**
 * A ParameterListener is notified whenever the value of a parameter changes.
 * @param <T> the value type parameter
 */
@FunctionalInterface
public interface ParameterListener<T> {
    /**
     * The method called when the value of a parameter changes.
     * @param parameter the parameter whose value changed
     * @param oldValue the old parameter value
     */
    void valueChanged(Parameter<T> parameter, T oldValue);
}
