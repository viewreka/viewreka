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
package org.beryx.viewreka.settings;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.beryx.viewreka.parameter.Parameter;

/**
 * The default implementation of the {@link ViewSettings} interface.
 */
public class ViewSettingsImpl implements ViewSettings {

    private long chartFrameDurationMillis = -1;
    private String selectedIteratedParameter;

    private Map<String, String> parameters = new LinkedHashMap<>();
    private Map<String, Serializable> properties = new LinkedHashMap<>();

    @Override
    public String getSelectedIteratedParameter() {
        return selectedIteratedParameter;
    }

    @Override
    public void setSelectedIteratedParameter(String parameterName) {
        this.selectedIteratedParameter = parameterName;
    }

    @Override
    public long getChartFrameDurationMillis() {
        return chartFrameDurationMillis;
    }

    @Override
    public void setChartFrameDurationMillis(long millis) {
        this.chartFrameDurationMillis = millis;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Sets the map of parameters
     * @param parameters a map of parameter values (as strings returned by {@link Parameter#getValueAsString()}) indexed by their names
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public Map<String, Serializable> getProperties() {
        return properties;
    }

    /**
     * Sets the map of properties
     * @param properties a map of property values indexed by their names
     */
    public void setProperties(Map<String, Serializable> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "{selectedIteratedParameter: " + selectedIteratedParameter + ", chartFrameDurationMillis: " + chartFrameDurationMillis + ", properties: " + properties + "}";
    }
}
