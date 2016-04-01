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

import java.util.Map;

import org.beryx.viewreka.parameter.Parameter;

/**
 * The model of a view belonging to a project model.
 */
public interface ViewModel {
    /**
     * @return the name of this view model
     */
    String getName();

    /**
     * @return the description of this view model
     */
    String getDescription();

    /**
     * Retrieves the datasets of this view model
     * @return a map of datasets indexed by their names
     */
    Map<String, Dataset> getDatasets();

    /**
     * Retrieves the parameters of thos view model.
     * @return a map of parameters indexed by their names
     */
    Map<String, Parameter<?>> getParameters();
}
