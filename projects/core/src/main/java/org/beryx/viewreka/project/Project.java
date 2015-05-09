package org.beryx.viewreka.project;

import org.beryx.viewreka.model.ProjectModel;
import org.beryx.viewreka.settings.ProjectSettings;
import org.beryx.viewreka.settings.SettingsManager;

public interface Project<V extends View> extends ProjectModel<V>{
	SettingsManager<ProjectSettings> getProjectSettingsManager();
}
