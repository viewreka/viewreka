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
