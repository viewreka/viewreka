package org.beryx.viewreka.model;

import org.beryx.viewreka.parameter.ParameterGroup;

public interface DataSource<Q extends Query> extends AutoCloseable {
	String getName();
	DatasetProvider getDatasetProvider(String providerName, Q query, ParameterGroup parameterGroup);
}
