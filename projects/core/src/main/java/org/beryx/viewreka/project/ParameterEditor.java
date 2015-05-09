package org.beryx.viewreka.project;

import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.parameter.ParameterListener;

public interface ParameterEditor<T> extends ParameterListener<T> {
	Parameter<T> getParameter();
	void updateEditor();
}
