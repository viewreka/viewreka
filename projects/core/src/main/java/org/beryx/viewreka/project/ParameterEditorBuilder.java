package org.beryx.viewreka.project;

import org.beryx.viewreka.parameter.Parameter;

public interface ParameterEditorBuilder<T, E extends ParameterEditor<T>, P> {
	 E createEditor(Parameter<T> parameter, P parentPane);
}
