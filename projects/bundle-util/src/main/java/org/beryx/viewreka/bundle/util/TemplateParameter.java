package org.beryx.viewreka.bundle.util;

import javafx.scene.control.Control;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface TemplateParameter {
    /**
     * @return the parameter name, as it appear in the corresponding {@link ParameterizedTemplate}
     */
    String getName();

    /**
     * @return a text describing the use and possible values of this parameter
     */
    String getDescription();

    /**
     * @return an example of a valid value for this parameter
     */
    String getSampleValue();

    /**
     * Performs validation checks for the value passed as argument and returns an error message if the validation failed.
     *
     * @param value the value to be validated
     * @return an error message if the validation failed or null if it succeeded
     */
    String getValidationErrorMessage(String value);

    Class<? extends Control> getControlType();

    Function<Control, String> getTextGetter();

    BiConsumer<Control, String> getTextSetter();
}
