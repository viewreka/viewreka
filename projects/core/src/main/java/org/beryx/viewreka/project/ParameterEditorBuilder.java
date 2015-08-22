package org.beryx.viewreka.project;

import org.beryx.viewreka.parameter.Parameter;

/**
 * A builder for creating parameter editors.
 * @param <T> the type of the parameter for which an editor is needed
 * @param <E> the type of the parameter editor to be created
 * @param <P> the type of the UI parent component (that is, the component having as child the newly created parameter editor)
 */
public interface ParameterEditorBuilder<T, E extends ParameterEditor<T>, P> {
     /**
     * Creates an editor for the specified parameter.
     * @param parameter the parameter for which an editor will be created
     * @param parentPane the component having as child the newly created parameter editor
     * @return the newly created parameter editor
     */
    E createEditor(Parameter<T> parameter, P parentPane);
}
