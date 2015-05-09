package org.beryx.viewreka.settings;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ViewSettingsImpl implements ViewSettings {

	private long selectedDelay = -1;
	private String selectedIteratedParameter;

	private Map<String, String> parameters = new LinkedHashMap<>();
	private Map<String, Serializable> properties = new LinkedHashMap<>();

	@Override
	public String getSelectedIteratedParameter() {
		return selectedIteratedParameter;
	}

	@Override
	public void setSelectedIteratedParameter(String parameterName) {
		this.selectedIteratedParameter = parameterName;
	}

	@Override
	public long getSelectedDelay() {
		return selectedDelay;
	}

	@Override
	public void setSelectedDelay(long delay) {
		this.selectedDelay = delay;
	}

	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	@Override
	public Map<String, Serializable> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Serializable> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		return "{selectedIteratedParameter: " + selectedIteratedParameter + ", selectedDelay: " + selectedDelay + ", properties: " + properties + "}";
	}
}
