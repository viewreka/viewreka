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
