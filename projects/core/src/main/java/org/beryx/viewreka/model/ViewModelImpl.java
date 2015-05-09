package org.beryx.viewreka.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.beryx.viewreka.core.Util;
import org.beryx.viewreka.parameter.Parameter;

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
		Map<String, Dataset> datasets = new LinkedHashMap<>();
		for(Entry<String, DatasetProvider> entry : datasetProviders.entrySet()) {
			datasets.put(entry.getKey(), entry.getValue().getDataset());
		}
		return datasets;
	}

	public Map<String, DatasetProvider> getDatasetProviders() {
		return datasetProviders;
	}

	@Override
	public Map<String, Parameter<?>> getParameters() {
		return parameters;
	}

}
