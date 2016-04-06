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

import java.util.List;
import java.util.Map;

/**
 * The model of a Viewreka project
 * @param <V> the model type of the views of this project model
 */
public interface ProjectModel<V extends ViewModel> {
    /**
     * @return the name of this project model
     */
    String getName();

    /**
     * @return the title of this project model
     */
    String getTitle();

    /**
     * @return the description of this project model
     */
    String getDescription();

    /**
     * @return the list of view models of this project model
     */
    List<V> getViews();

    /**
     * Retrieves the data sources associated with this project model.
     * @return a map of data sources indexed by their names
     */
    Map<String, DataSource<? extends Query>> getDataSources();
}