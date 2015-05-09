package org.beryx.viewreka.parameter;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.model.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Parameter<T> extends Parameterized, Comparable<Parameter<T>> {
	static final Logger _log = LoggerFactory.getLogger(Parameter.class);

	public static class Value<TT> {
		private final TT value;
		private final String textValue;
		private final String displayedValue;

		public Value(TT value, String textValue, String displayedValue) {
			this.value = value;
			this.textValue = textValue;
			this.displayedValue = displayedValue;
		}

		public TT getValue() {
			return value;
		}

		public String getTextValue() {
			return textValue;
		}

		public String getDisplayedValue() {
			return displayedValue;
		}

		@Override
		public String toString() {
			return textValue;
		}
	}

	ParameterGroup getParameterGroup();
	void parameterGroupChanged();
	String getName();
	Class<T> getValueClass();
	T getValue();
	void setValue(T value);

	T toValue(String sVal) throws Exception;
	String asString(T value);

	default String getValueAsString() {
		return asString(getValue());
	}

	default T toValidValue(String sVal) {
		T newValue;
		try {
			newValue = toValue(sVal);
		} catch (Exception e) {
			throw new ViewrekaException("Parameter " + getName() + ": cannot convert '" + sVal + "' to " + getValueClass().getSimpleName(), e);
		}
		String errMsg = getValidationErrorMessage(newValue);
		if(errMsg != null) throw new ViewrekaException("Invalid value for parameter " + getName() + ": " + errMsg);
		return newValue;
	}

	default void setValueFromString(String sVal) {
		setValue(toValidValue(sVal));
	}

	boolean isNullAllowed();

	default String getValidationErrorMessage(T value) {
		if((value == null) && !isNullAllowed()) return "Null value not permitted for paramater " + getName();
		if(isIterable() && !getPossibleValues().stream().anyMatch(v -> Objects.equals(v.value, value))) {
			return "'" + value + "' is not a possibble value for parameter " +  getName();
		}
		return null;
	}

	default boolean isValid() {
		String errMsg = getValidationErrorMessage(getValue());
		if(errMsg != null) _log.trace("Validation error message for parameter {}: {}", getName(), errMsg);
		return errMsg == null;
	}

	default Set<Parameter<?>> getPrerequisiteParameters() {
		Set<String> names = getParameterNames();
		if(names.isEmpty()) return Collections.emptySet();

		Set<Parameter<?>> prerequisites = new TreeSet<>();

		LinkedList<Iterator<String>> stack = new LinkedList<>();
		stack.push(names.iterator());

		while(!stack.isEmpty()) {
			if(!stack.peek().hasNext()) {
				stack.pop();
				continue;
			}
			String name = stack.peek().next();
			if(getName().equals(name)) throw new ViewrekaException("Cycle detected while getting the prerequisite parameters of " + getName());
			Parameter<?> parameter = getParameterGroup().getParameter(name);
			if(parameter == null) {
				throw new ViewrekaException("Unknown prerequisite parameter of " + getName() + ": " + name);
			}
			boolean added = prerequisites.add(parameter);
			if(added) {
				stack.push(parameter.getParameterNames().iterator());
			}
		}
		return prerequisites;
	}

	default Set<Parameter<?>> getAffectedParameters() {
		Set<Parameter<?>> affected = new TreeSet<>();
		for(Parameter<?> parameter : getParameterGroup().getParameters()) {
			if(getName().equals(parameter.getName())) continue;
			if(parameter.getPrerequisiteParameters().contains(this)) {
				affected.add(parameter);
			}
		}
		_log.trace("Parameters affected by {}: {}", getName(), affected);
		return affected;
	}

	boolean isIterable();
	List<Value<T>> getPossibleValues();
	default T setPossibleValue(int index) {
		T value = getPossibleValues().get(index).getValue();
		setValue(value);
		return value;
	}

	/** Invalidates the parameter. This will force the parameter to refresh its value and its possible values.
	 * Typically, this method is called to notify that the value of a prerequisite parameter has changed. */
	void invalidate();

	boolean addListener(ParameterListener<T> listener);
	boolean removeListener(ParameterListener<T> listener);
}
