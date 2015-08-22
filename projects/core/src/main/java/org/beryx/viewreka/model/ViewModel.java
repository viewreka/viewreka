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
