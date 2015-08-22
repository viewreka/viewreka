package org.beryx.viewreka.fxui.editor;

import javafx.scene.Parent;

import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.project.ParameterEditorBuilder;

/**
 * A builder for creating JavaFX parameter editors.
 * @param <T> the type of the parameter for which an editor is needed
 */
public interface FxParameterEditorBuilder<T> extends ParameterEditorBuilder<T, FxParameterEditor<T>, Parent> {
    /**
     * Creates a JavaFX parameter editor for the specified parameter
     * @param parameter the parameter for which an editor will be created
     * @return the newly created parameter editor
     */
    default FxParameterEditor<T> getEditor(Parameter<T> parameter) {
        FxParameterEditor<T> editor = createEditor(parameter, null);
        editor.load();
        return editor;
    }
}
