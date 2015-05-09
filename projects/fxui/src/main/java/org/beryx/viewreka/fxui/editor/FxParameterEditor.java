package org.beryx.viewreka.fxui.editor;

import javafx.scene.layout.FlowPane;

import org.beryx.viewreka.fxui.FXMLControl;
import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.project.ParameterEditor;

public abstract class FxParameterEditor<T> extends FlowPane implements FXMLControl, ParameterEditor<T> {
	protected final Parameter<T> parameter;

	public FxParameterEditor(Parameter<T> parameter) {
		this.parameter = parameter;
	}

	@Override
	public Parameter<T> getParameter() {
		return parameter;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + parameter.getName() + "]";
	}
}
