package org.beryx.viewreka.project;

import org.beryx.viewreka.bundle.api.ViewrekaBundle;
import org.beryx.viewreka.model.ProjectModel;
import org.beryx.viewreka.settings.ProjectSettings;
import org.beryx.viewreka.settings.SettingsManager;

import java.util.List;

/**
 * This interface, which extends {@link ProjectModel}, must be implemented by any Viewreka project.
 * @param <V> the type of the views of this project
 */
public interface Project<V extends View, GUI> extends ProjectModel<V>{
    /**
     * Retrieves the settings manager of this project
     * @return the settings manager of this project
     */
    SettingsManager<ProjectSettings> getProjectSettingsManager();

    /**
     * @return the list of {@link ViewrekaBundle}s used by this project
     */
    List<ViewrekaBundle<GUI>> getBundles();
}
