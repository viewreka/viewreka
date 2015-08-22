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
