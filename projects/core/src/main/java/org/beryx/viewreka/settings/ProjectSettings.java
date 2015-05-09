package org.beryx.viewreka.settings;

import static org.beryx.viewreka.core.Util.getValue;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ProjectSettings {
	static final Logger _log = LoggerFactory.getLogger(ProjectSettings.class);

	String getCurrentView();
	void setCurrentView(String currentView);

	/** @return - the map of {@link ViewSettings} indexed by view names */
	Map<String, ViewSettings> getViewSettings();

	/**
	 * @return - the {@link ViewSettings} of the view specified as argument.
	 * If no settings are available for this view, the method creates and stores a new {@link ViewSettings} instance.
	 */
	ViewSettings getOrCreateViewSetting(String viewName);

	Map<String, Serializable> getProperties();

	default <T> T getProperty(String name, T defaultValue, boolean allowNull) {
		return getValue(getProperties(), name, defaultValue, allowNull);
	}

	default <T extends Serializable> Object setProperty(String name, T value) {
		_log.debug("Property " + name + " set to " + value);
		return getProperties().put(name, value);
	}
}
