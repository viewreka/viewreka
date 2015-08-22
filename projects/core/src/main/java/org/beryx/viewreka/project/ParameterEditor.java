package org.beryx.viewreka.project;

import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.parameter.ParameterListener;

/**
 * An implementation of this interface is typically a GUI component that allows editing the value of a {@link Parameter}.
 * The {@link ParameterEditor} is however a minimal interface, which makes no assumptions about the GUI toolkit used to implement it.
 * @param <T> - the type of the parameter
 */
public interface ParameterEditor<T> extends ParameterListener<T> {
    /**
     * Retrieves the parameter provided by this editor.
     * @return the parameter provided by this editor.
     */
    Parameter<T> getParameter();

    /**
     * Updates the GUI component associated with this editor.
     * A call to this method is typically triggered by a change in the parameter handled by this editor.
     */
    void updateEditor();
}
