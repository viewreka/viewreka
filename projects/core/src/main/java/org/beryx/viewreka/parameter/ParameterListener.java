package org.beryx.viewreka.parameter;

@FunctionalInterface
public interface ParameterListener<T> {
	void valueChanged(Parameter<T> parameter, T oldValue);
}
