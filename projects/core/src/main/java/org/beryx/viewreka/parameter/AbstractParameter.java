/**
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.viewreka.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.model.Dataset;
import org.beryx.viewreka.model.DatasetProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class that implements all methods of the {@link Parameter} interface, except {@link #getValueClass()} and {@link #toValue(String)}.
 * <br/>It adds support for specifying minimum and maximum values and for caching possible values, prerequisite parameters and affected parameters.
 * <br/>If available, a data set is used for retrieving the list of possible values.
 * @param <T> the parameter value type
 */
public abstract class AbstractParameter<T> implements Parameter<T> {
    private static final Logger log = LoggerFactory.getLogger(AbstractParameter.class);

    private final String name;
    private final Class<T> type;
    private final ParameterGroup parameterGroup;
    private final Comparator<T> valueComparator;

    private final boolean nullAllowed; // default: false

    private final T minValue; // default: null
    private final T maxValue; // default: null
    private final boolean minValueAllowed; // default: true
    private final boolean maxValueAllowed; // default: true

    private final DatasetProvider datasetProvider; // default: null
    private final int valColumn; // default: 1
    private final String valColumnName; // default: null
    private final int textValColumn; // default: -1
    private final String textValColumnName; // default: null
    private final int displayedValColumn; // default: -1
    private final String displayedValColumnName; // default: null

    private final List<ParameterListener<T>> listeners = new ArrayList<>();

    private T value;
    private int possibleValueIndex = -1;
    private List<Value<T>> cachedPossibleValues = null;

    private Set<Parameter<?>> cachedPrerequisiteParameters = null;
    private Set<Parameter<?>> cachedAffectedParameters = null;


    /**
     * A builder class used as argument in the constructor of {@link AbstractParameter}.
     * @param <TT> the parameter value type
     * @param <PP> the parameter type
     */
    public abstract static class Builder<TT, PP extends AbstractParameter<TT>> {
        private final String name;
        private final Class<TT> type;
        private final ParameterGroup parameterGroup;
        private final Comparator<TT> valueComparator;

        private boolean nullAllowed = false;

        private TT minValue = null;
        private TT maxValue = null;
        private boolean minValueAllowed = true;
        private boolean maxValueAllowed = true;

        private DatasetProvider datasetProvider = null;
        private int valColumn = 1;
        private String valColumnName = null;
        private int textValColumn = -1;
        private String textValColumnName = null;
        private int displayedValColumn = -1;
        private String displayedValColumnName = null;

        /**
         * Creates a new parameter.
         * @return the created parameter
         */
        abstract public PP build();

        public Builder(String name, Class<TT> type, ParameterGroup parameterGroup, Comparator<TT> valueComparator) {
            this.name = name;
            this.type = type;
            this.parameterGroup = parameterGroup;
            this.valueComparator = valueComparator;
        }

        public Builder<TT, PP> nullAllowed(boolean val) { this.nullAllowed = val; return this; }
        public Builder<TT, PP> minValue(TT val) { this.minValue = val; return this; }
        public Builder<TT, PP> maxValue(TT val) { this.maxValue = val; return this; }
        public Builder<TT, PP> minValueAllowed(boolean val) { this.minValueAllowed = val; return this; }
        public Builder<TT, PP> maxValueAllowed(boolean val) { this.maxValueAllowed = val; return this; }
        public Builder<TT, PP> datasetProvider(DatasetProvider val) { this.datasetProvider = val; return this; }
        public Builder<TT, PP> valColumn(int val) { this.valColumn = val; return this; }
        public Builder<TT, PP> valColumnName(String val) { this.valColumnName = val; return this; }
        public Builder<TT, PP> textValColumn(int val) { this.textValColumn = val; return this; }
        public Builder<TT, PP> textValColumnName(String val) { this.textValColumnName = val; return this; }
        public Builder<TT, PP> displayedValColumn(int val) { this.displayedValColumn = val; return this; }
        public Builder<TT, PP> displayedValColumnName(String val) { this.displayedValColumnName = val; return this; }

        public Builder<TT, PP> possibleValuesSource(DatasetProvider provider, int[] columns) {
            this.datasetProvider = provider;
            if((columns == null) || (columns.length == 0)) {
                valColumn = 1;
                textValColumn = -1;
                displayedValColumn = -1;
            } else {
                valColumn = columns[0];
                textValColumn = (columns.length > 1) ? columns[1] : -1;
                displayedValColumn = (columns.length > 2) ? columns[2] : -1;
            }
            return this;
        }
    }

    protected AbstractParameter(Builder<T, ? extends AbstractParameter<T>> builder) {
        this.name = builder.name;
        this.type = builder.type;
        this.parameterGroup = builder.parameterGroup;
        this.valueComparator = builder.valueComparator;


        this.nullAllowed = builder.nullAllowed;

        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
        this.minValueAllowed = builder.minValueAllowed;
        this.maxValueAllowed = builder.maxValueAllowed;

        this.datasetProvider = builder.datasetProvider;
        if(datasetProvider != null) {
            datasetProvider.addDirtyListener(provider -> clearCachedPossibleValues());
        }

        this.valColumn = builder.valColumn;
        this.valColumnName = builder.valColumnName;
        this.textValColumn = builder.textValColumn;
        this.textValColumnName = builder.textValColumnName;
        this.displayedValColumn = builder.displayedValColumn;
        this.displayedValColumnName = builder.displayedValColumnName;
    }

    @Override
    public int compareTo(Parameter<T> prm) {
        if(value == null) return (prm.getValue() == null) ? 0 : -1;
        if(prm.getValue() == null) return 1;
        if(!getClass().isAssignableFrom(prm.getClass())) {
            int thisIndex = parameterGroup.getParameterIndex(getName());
            int otherIndex = parameterGroup.getParameterIndex(prm.getName());
            return Integer.compare(thisIndex, otherIndex);
        }
        try {
            return valueComparator.compare(getValue(), prm.getValue());
        } catch(Exception e) {
            throw new ViewrekaException("Failed to compare " + this + " and " + prm, e);
        }
    }


    @Override
    public String getValidationErrorMessage(T val) {
        String errMsg = Parameter.super.getValidationErrorMessage(val);
        if((errMsg == null) && (val != null)) {
            boolean err = false;
            if(minValue != null) {
                int cmp = valueComparator.compare(val, minValue);
                if((cmp < 0) || (!minValueAllowed && (cmp == 0))) err = true;
            }
            if(maxValue != null) {
                int cmp = valueComparator.compare(val, maxValue);
                if((cmp > 0) || (!maxValueAllowed && (cmp == 0))) err = true;
            }
            if(err) {
                String lPar = minValueAllowed ? "[" : "(";
                String rPar = maxValueAllowed ? "]" : ")";
                errMsg = "Value outside of the allowed range " + lPar + minValue + ", " + maxValue + rPar + " for parameter " + getName() + ": " + val;
            }
        }
        return errMsg;
    }

    /**
     * Returns the data set used to retrieve the list of possible values f this parameter.
     * @return the data set
     */
    public Dataset getDataset() {
        if(datasetProvider == null) return null;
        return datasetProvider.getDataset();
    }

    @Override
    public ParameterGroup getParameterGroup() {
        return parameterGroup;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        if(Objects.equals(value, this.value)) return;
        T oldValue = this.value;
        this.value = value;

        log.debug("Changed value of {} in dataset {} from '{}' to '{}'. Affected parameters: {}", name,
                (datasetProvider == null ? null : datasetProvider.getName()), oldValue, value, getAffectedParameters());

        invalidate();
        for(Parameter<?> prm : getAffectedParameters()) {
            prm.invalidate();
            prm.setValue(null);
        }
        for(ParameterListener<T> listener : new ArrayList<>(listeners)) {
            listener.valueChanged(this, oldValue);
        }
    }

    @Override
    public T setPossibleValue(int index) {
        T newValue = Parameter.super.setPossibleValue(index);
        possibleValueIndex = index;
        return newValue;
    }

    @Override
    public String getValueAsString() {
        if(possibleValueIndex >= 0) return getPossibleValues().get(possibleValueIndex).getTextValue();
        return asString(value);
    }

    @Override
    public String asString(T val) {
        if(val != null && isIterable()) {
            int pos = getPossibleValues().stream().map(Value::getValue).collect(Collectors.toList()).indexOf(val);
            if(pos >= 0) {
                return getPossibleValues().get(pos).getTextValue();
            }
        }
        return (val == null) ? "" : val.toString();
    }

    @Override
    public boolean isNullAllowed() {
        return nullAllowed;
    }


    /**
     * Retrieves the minimum value permitted for this parameter. Note that this value is taken into consideration only if {@link #isMinValueAllowed()} returns true.
     * @return the minimum value
     */
    public T getMinValue() {
        return minValue;
    }

    /**
     * Indicates whether this parameter accepts a minimum value.
     * @return true, if {@code minValue} is allowed for this parameter.
     */
    public boolean isMinValueAllowed() {
        return minValueAllowed;
    }

    /**
     * Retrieves the maximum value permitted for this parameter. Note that this value is taken into consideration only if {@link #isMaxValueAllowed()} returns true.
     * @return the maximum value
     */
    public T getMaxValue() {
        return maxValue;
    }

    /**
     * Indicates whether this parameter accepts a maximum value.
     * @return true, if {@code maxValue} is allowed for this parameter.
     */
    public boolean isMaxValueAllowed() {
        return maxValueAllowed;
    }

    @Override
    public boolean isIterable() {
        return (datasetProvider != null);
    }

    @Override
    public List<Value<T>> getPossibleValues() {
        if(datasetProvider == null) return Collections.emptyList();
        synchronized(datasetProvider) {
            if(cachedPossibleValues == null) {
                List<Value<T>> values = new ArrayList<>();
                try(Dataset dataset = datasetProvider.getDataset()) {
                    for(int row=0; row < dataset.getRowCount(); row++) {
                        T val = (valColumn >= 0) ? dataset.getValue(row, valColumn, type) : dataset.getValue(row, valColumnName, type);
                        String textValue = (textValColumn >= 0)
                                ? dataset.getString(row, textValColumn)
                                : (textValColumnName != null)
                                ? dataset.getString(row, textValColumnName)
                                : (val == null) ? "" : val.toString();
                        String displayedValue = (displayedValColumn >= 0)
                                ? dataset.getString(row, displayedValColumn)
                                : (displayedValColumnName != null)
                                ? dataset.getString(row, displayedValColumnName)
                                : textValue;
                        values.add(new Value<>(val, textValue, displayedValue));
                    }
                } catch (Exception e) {
                    throw new ViewrekaException("Cannot retrieve the possible values of parameter " + getName(), e);
                }
                cachedPossibleValues = values;
            }
            return cachedPossibleValues;
        }
    }

    @Override
    public Set<String> getParameterNames() {
        return (datasetProvider == null) ? Collections.emptySet() : datasetProvider.getParameterNames();
    }

    @Override
    public void parameterGroupChanged() {
        clearCachedParameters();
    }

    /**
     * Clears the cached sets of prerequisite and affected parameters.
     */
    public void clearCachedParameters() {
        cachedPrerequisiteParameters = null;
        cachedAffectedParameters = null;
    }

    /**
     * Clears the cached list of possible values.
     */
    public void clearCachedPossibleValues() {
        cachedPossibleValues = null;
    }

    @Override
    public Set<Parameter<?>> getPrerequisiteParameters() {
        if(cachedPrerequisiteParameters == null) {
            cachedPrerequisiteParameters = Parameter.super.getPrerequisiteParameters();
        }
        return cachedPrerequisiteParameters;
    }

    @Override
    public Set<Parameter<?>> getAffectedParameters() {
        if(cachedAffectedParameters == null) {
            cachedAffectedParameters = Parameter.super.getAffectedParameters();
        }
        return cachedAffectedParameters;
    }

    @Override
    public void invalidate() {
        possibleValueIndex = -1;
        if(datasetProvider != null) {
            datasetProvider.setDirty();
        }
    }

    @Override
    public boolean addListener(ParameterListener<T> listener) {
        return listeners.add(listener);
    }

    @Override
    public boolean removeListener(ParameterListener<T> listener) {
        return listeners.remove(listener);
    }

    @Override
    public String toString() {
        return name + ": " + getValueAsString();
    }
}
