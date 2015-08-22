package org.beryx.viewreka.parameter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.model.Parameterized;

/**
 * The group of parameters associated with a view.
 */
public class ParameterGroup implements Parameterized {
    private final Map<String, Parameter<?>> parameterMap = new LinkedHashMap<>();

    @Override
    public Set<String> getParameterNames() {
        return Collections.unmodifiableSet(parameterMap.keySet());
    }

    /**
     * Retrieves the parameters of this group
     * @return the parameters of this group
     */
    public Collection<Parameter<?>> getParameters() {
        return Collections.unmodifiableCollection(parameterMap.values());
    }

    /**
     * Retrieves a parameter with a specified name
     * @param name the name of the desired parameter
     * @return the parameter with the specified name
     */
    public Parameter<?> getParameter(String name) {
        return parameterMap.get(name);
    }

    /**
     * Retrieves the index of the parameter with the specified name.
     * @param name the name of the desired parameter
     * @return the parameter index or -1 if no parameter with the specified name is present in this group.
     */
    public int getParameterIndex(String name) {
        return Arrays.binarySearch(parameterMap.keySet().toArray(), name);
    }

    /**
     * Adds a parameter to this group. Implementations should throw a ViewrekaException if a parameter with the same name already exists in this group.
     * Typically, this method will notify all current parameters in this group that the group has changed, by calling their {@link Parameter#parameterGroupChanged()} method.
     * @param parameter the parameter to be added
     */
    public void addParameter(Parameter<?> parameter) {
        Parameter<?> oldParameter = parameterMap.put(parameter.getName(), parameter);
        if(oldParameter != null) {
            throw new ViewrekaException("Parameter " + parameter.getName() + " already defined.");
        }
        parameterMap.values().forEach(Parameter::parameterGroupChanged);
    }

    @Override
    public String toString() {
        return parameterMap.toString();
    }
}
