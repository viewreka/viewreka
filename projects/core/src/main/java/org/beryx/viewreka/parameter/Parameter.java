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

/**
 * Each project view is characterized by a (possibly empty) set of parameters. Typically, parameters are used to configure the queries needed to retrieve the view data sets.
 * <br/>All parameters of a view belong to the same {@link ParameterGroup}.
 * <br/>A parameter may specify a list of possible values. In this case, the parameter is <em>iterable</em>.
 * @param <T> the parameter value type
 */
public interface Parameter<T> extends Parameterized, Comparable<Parameter<T>> {
    static final Logger _log = LoggerFactory.getLogger(Parameter.class);


    /**
     * A 3-tuple {@code (value, textValue, displayedValue)} used to describe one of the possible values of a parameter.
     * @param <TT> the parameter value type
     */
    public static class Value<TT> {
        private final TT value;
        private final String textValue;
        private final String displayedValue;

        public Value(TT value, String textValue, String displayedValue) {
            this.value = value;
            this.textValue = textValue;
            this.displayedValue = displayedValue;
        }

        /**
         * @return this possible value in its unaltered form
         */
        public TT getValue() {
            return value;
        }

        /**
         * @return the text representation of this possible value.
         * If {@link Parameter#toValue(String)} is called with the text representation as argument, it should return this possible value in its unaltered form.
         */
        public String getTextValue() {
            return textValue;
        }

        /**
         * @return a text representation of this possible value, suitable to be displayed in a GUI (for example, as a combobox option).
         */
        public String getDisplayedValue() {
            return displayedValue;
        }

        @Override
        public String toString() {
            return textValue;
        }
    }

    /**
     * Retrieves the parameter group containing this parameter. All parameters of a view belong to the same group.
     * @return the group to which this parameter belong.
     */
    ParameterGroup getParameterGroup();

    /**
     * This method is called whenever the group containing this parameter has changed (for example, when a new parameter has been added to the group).
     */
    void parameterGroupChanged();


    /**
     * @return the name of this parameter
     */
    String getName();

    /**
     * @return the value type of this parameter
     */
    Class<T> getValueClass();


    /**
     * @return the value of this parameter, in its unaltered form
     */
    T getValue();


    /**
     * Changing the value of a parameter will normally trigger a series of events.
     * A typical implementation will {@link #invalidate()} this parameter and all its {@code affectedParameters}
     * and it will also call the {@link ParameterListener#valueChanged(Parameter, Object)} method on all its listeners.
     * @param value the new value
     */
    void setValue(T value);

    /**
     * Converts the text representation passed as argument to a value of type {@code T}.
     * @param sVal the text representation to be converted
     * @return the converted value of type {@code T}
     * @throws Exception if the conversion failed.
     */
    T toValue(String sVal) throws Exception;

    /**
     * Provides a text representation of the {@code value}. The text representation should be suitable to be passed as argument to {@link #toValue(String)}.
     * @param value a value of type {@code T}.
     * @return the text representation of the {@code value}.
     */
    String asString(T value);

    /**
     * Provides the text representation of the value of this parameter. The text representation should be suitable to be passed as argument to {@link #toValue(String)}.
     * @return the text representation
     */
    default String getValueAsString() {
        return asString(getValue());
    }

    /**
     * Converts the text representation passed as argument to a valid value of type {@code T}.
     * @param sVal the text representation to be converted
     * @return the converted value
     * @throws ViewrekaException if the conversion or the validation of the converted value failed.
     */
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

    /**
     * Sets the value of this parameter in accordance to the text representation passed as argument.
     * @param sVal the text representation of the value to be set
     * @throws ViewrekaException if the conversion or the validation of the converted value failed.
     */
    default void setValueFromString(String sVal) {
        setValue(toValidValue(sVal));
    }

    /**
     * Indicates whether null is a valid value for this parameter.
     * @return true, if null is a valid value for this parameter
     */
    boolean isNullAllowed();

    /**
     * Performs validation checks for the value passed as argument and returns an error message if the validation failed.
     * @param value the value to be validated
     * @return an error message if the validation failed or null if it succeeded
     */
    default String getValidationErrorMessage(T value) {
        if(value == null) return isNullAllowed() ? null : "Null value not permitted for paramater " + getName();
        if(isIterable() && !getPossibleValues().stream().anyMatch(v -> Objects.equals(v.value, value))) {
            return "'" + value + "' is not a possibble value for parameter " +  getName();
        }
        return null;
    }

    /**
     * @return true, if the current value of this parameter passes the validation checks
     */
    default boolean isValid() {
        String errMsg = getValidationErrorMessage(getValue());
        if(errMsg != null) _log.trace("Validation error message for parameter {}: {}", getName(), errMsg);
        return errMsg == null;
    }

    /**
     * Retrieves the set of parameters on which this parameter depends.
     * Typically, these parameters are those needed to retrieve the list of possible values of this parameter
     * (for example, they may configure the query used to create a data set containing the possible values).
     * @return the set of prerequisite parameters
     */
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

    /**
     * Retrieves the set of parameters that depend of the value of this parameter. Typically, these are all parameters having this parameter as prerequisite.
     * @return the set of affected parameters
     */
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

    /**
     * A parameter is {@code iterable}, if it is in principle able to provide a list of possible values.
     * Note that this method may return true, even if the list of possible values is not available at the time of invocation
     * (due, for example, to the fact that the prerequisite parameters have no valid values).
     * @return true, if this parameter is iterable
     */
    boolean isIterable();

    /**
     * Retrieves the list of possible values for this parameter. If the parameter is not iterable, an empty list should be returned.
     * @return the list of possible values
     */
    List<Value<T>> getPossibleValues();
    default T setPossibleValue(int index) {
        T value = getPossibleValues().get(index).getValue();
        setValue(value);
        return value;
    }

    /**
     * Invalidates the parameter. This will force the parameter to refresh its value and its possible values.
     * Typically, this method is called to notify that the value of a prerequisite parameter has changed.
    */
    void invalidate();

    /**
     * Adds a parameter listener to this parameter
     * @param listener the parameter listener to be added.
     * @return true, if the parameter listener has been successfully added.
     */
    boolean addListener(ParameterListener<T> listener);

    /**
     * Removes a parameter listener from this parameter
     * @param listener the parameter listener to be removed.
     * @return true, if the parameter listener has been found and successfully removed.
     */
    boolean removeListener(ParameterListener<T> listener);
}
