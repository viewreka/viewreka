package org.beryx.viewreka.fxui.editor;

import javafx.scene.Parent;

import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.project.ParameterEditorBuilder;

public interface FxParameterEditorBuilder<T> extends ParameterEditorBuilder<T, FxParameterEditor<T>, Parent> {
	default FxParameterEditor<T> getEditor(Parameter<T> parameter) {
		FxParameterEditor<T> editor = createEditor(parameter, null);
		editor.load();
		return editor;
	}
}
