/**
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.viewreka.bundle.util;

import javafx.scene.Node;

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

    Class<? extends Node> getControlType();

    Function<Node, String> getTextGetter();

    BiConsumer<Node, String> getTextSetter();
}
