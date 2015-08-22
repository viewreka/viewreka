package org.beryx.viewreka.fxui.editor;

import javafx.scene.layout.FlowPane;

import org.beryx.viewreka.fxui.FXMLNode;
import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.project.ParameterEditor;

/**
 * An abstract JavaFX parameter editor
 * @param <T>
 */
public abstract class FxParameterEditor<T> extends FlowPane implements FXMLNode, ParameterEditor<T> {
    protected final Parameter<T> parameter;

    /**
     * @param parameter the parameter handled by this editor
     */
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
