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
