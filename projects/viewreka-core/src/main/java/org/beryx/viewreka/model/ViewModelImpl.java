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
package org.beryx.viewreka.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.beryx.viewreka.core.Util;
import org.beryx.viewreka.parameter.Parameter;

/**
 * The default implementation of the {@link ViewModel} interface.
 */
public class ViewModelImpl implements ViewModel {
    private final String name;
    private final String description;

    private final Map<String, DatasetProvider> datasetProviders = new LinkedHashMap<>();
    private final Map<String, Parameter<?>> parameters = new LinkedHashMap<>();

    public ViewModelImpl(String name, String description) {
        this.name = Util.requireNonNull(name, "View name");
        this.description = (description != null) ? description : name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Map<String, Dataset> getDatasets() {
        return datasetProviders.entrySet().stream().collect(
            Collectors.toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue().getDataset()
            )
        );
    }

    /**
     * Retrieves the map of dataset providers of this view model.
     * @return a map of dataset providers indexed by their names.
     */
    public Map<String, DatasetProvider> getDatasetProviders() {
        return datasetProviders;
    }

    @Override
    public Map<String, Parameter<?>> getParameters() {
        return parameters;
    }

}
