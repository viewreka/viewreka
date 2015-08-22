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
