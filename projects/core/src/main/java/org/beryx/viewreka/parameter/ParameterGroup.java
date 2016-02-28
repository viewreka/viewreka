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
