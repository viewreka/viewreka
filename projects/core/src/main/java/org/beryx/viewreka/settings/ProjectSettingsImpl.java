package org.beryx.viewreka.settings;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The default implementation of the {@link ProjectSettings} interface.
 */
public class ProjectSettingsImpl implements ProjectSettings {

    private String currentView;
    private Map<String, ViewSettings> viewSettings = new LinkedHashMap<>();

    private Map<String, Serializable> properties = new LinkedHashMap<>();

    @Override
    public String getCurrentView() {
        return currentView;
    }

    @Override
    public void setCurrentView(String currentView) {
        this.currentView = currentView;
    }

    @Override
    public Map<String, Serializable> getProperties() {
        return properties;
    }

    /**
     * Sets the map of properties
     * @param properties a map of property values indexed by their names
     */
    public void setProperties(Map<String, Serializable> properties) {
        this.properties = properties;
    }

    @Override
    public Map<String, ViewSettings> getViewSettings() {
        return viewSettings;
    }

    /**
     * Sets the map of view settings
     * @param viewSettings a map of view settings indexed by their names
     */
    public void setViewSettings(Map<String, ViewSettings> viewSettings) {
        this.viewSettings = viewSettings;
    }

    @Override
    public String toString() {
        return "{currentView: " + currentView + ", properties: " + properties + ", viewSettings" + viewSettings + "}";
    }

    @Override
    public ViewSettings getOrCreateViewSetting(String viewName) {
        ViewSettings settings = viewSettings.get(viewName);
        if(settings == null) {
            settings = new ViewSettingsImpl();
            viewSettings.put(viewName, settings);
        }
        return settings;
    }
}
