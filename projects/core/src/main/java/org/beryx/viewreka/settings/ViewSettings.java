package org.beryx.viewreka.settings;

import static org.beryx.viewreka.core.Util.getValue;

import java.io.Serializable;
import java.util.Map;

public interface ViewSettings {
	/** @return - the map of parameter values (as strings) indexed by parameter names. */
	Map<String, String> getParameters();

	String getSelectedIteratedParameter();
	void setSelectedIteratedParameter(String parameterName);

	long getSelectedDelay();
	void setSelectedDelay(long delay);

	Map<String, Serializable> getProperties();

	default <T> T getProperty(String name, T defaultValue, boolean allowNull) {
		return getValue(getProperties(), name, defaultValue, allowNull);
	}

	default <T extends Serializable> Object setProperty(String name, T value) {
		return getProperties().put(name, value);
	}
}
