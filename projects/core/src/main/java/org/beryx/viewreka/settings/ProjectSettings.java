package org.beryx.viewreka.settings;

import static org.beryx.viewreka.core.Util.getValue;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for accessing the settings of a Viewreka project.
 */
public interface ProjectSettings {
    static final Logger _log = LoggerFactory.getLogger(ProjectSettings.class);

    /**
     * Retrieves the last selected view
     * @return the name of the last selected view
     */
    String getCurrentView();

    /**
     * Sets the current view
     * @param currentView the current view
     */
    void setCurrentView(String currentView);

    /**
     * Retrieves the view settings
     * @return the map of {@link ViewSettings} indexed by view names
     */
    Map<String, ViewSettings> getViewSettings();

    /**
     * Retrieves the settings of the specified view.
     * @param viewName the name of the view for which the settings will be retrieved
     * @return - the {@link ViewSettings} of the view specified as argument.
     * If no settings are available for this view, the method creates and stores a new {@link ViewSettings} instance.
     */
    ViewSettings getOrCreateViewSetting(String viewName);

    /**
     * Retrieves the properties of this project.
     * @return the map of project properties indexed by property names
     */
    Map<String, Serializable> getProperties();

    /**
     * Retrieves the property with the specified name or a default value.
     * @param name the name of the property to be retrieved
     * @param defaultValue the default value to be returned if the specified property is not available.
     * @param allowNull true, if null is a permitted value for the specified property
     * @return the value of the property with the specified name or a default value if no such property exists.
     * The default value is also returned if the value of the specified property is null and {@code allowNull} is false.
     */
    default <T> T getProperty(String name, T defaultValue, boolean allowNull) {
        return getValue(getProperties(), name, defaultValue, allowNull);
    }

    /**
     * Sets the value of a specified property.
     * @param name the name of the property to be set
     * @param value the value to be set
     * @return the previous value of the property with the specified name
     */
    default <T extends Serializable> Object setProperty(String name, T value) {
        _log.debug("Property " + name + " set to " + value);
        return getProperties().put(name, value);
    }
}
