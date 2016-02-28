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
package org.beryx.viewreka.fxui.editor;

import javafx.scene.layout.FlowPane;

import org.beryx.viewreka.fxcommons.FXMLNode;
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
