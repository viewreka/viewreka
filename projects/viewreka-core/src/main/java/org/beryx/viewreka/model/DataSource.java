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

import org.beryx.viewreka.parameter.ParameterGroup;

/**
 * A data source that can create dataset providers based on parameterized queries.
 * @param <Q> the type of queries accepted by this data source.
 */
public interface DataSource<Q extends Query> extends AutoCloseable {
    /*
     * @return the name of this data source
     */
    String getName();

    /**
     * Retrieves a dataset provider based on the specified parameterized query.
     * @param providerName the name of the provider to be retrieved
     * @param query the query to be used to retrieve the dataset provider
     * @param parameterGroup the group of parameters used to configure the query
     * @return the dataset provider
     */
    DatasetProvider getDatasetProvider(String providerName, Q query, ParameterGroup parameterGroup);
}
