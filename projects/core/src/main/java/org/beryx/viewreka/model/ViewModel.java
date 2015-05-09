package org.beryx.viewreka.model;

import java.util.Map;

import org.beryx.viewreka.parameter.Parameter;

public interface ViewModel {
	String getName();
	String getDescription();

	Map<String, Dataset> getDatasets();
	Map<String, Parameter<?>> getParameters();
}
