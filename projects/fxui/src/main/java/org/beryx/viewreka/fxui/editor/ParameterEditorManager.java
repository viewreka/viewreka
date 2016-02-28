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

import java.util.LinkedHashMap;
import java.util.Map;

import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.project.ParameterEditor;
import org.beryx.viewreka.project.ParameterEditorBuilder;

/**
 * A manager of parameter editor builders, which is able to provide the appropriate editor builder for a specified parameter.
 */
public class ParameterEditorManager {
    private final Map<String, FxParameterEditorBuilder<?>> buildersByParameter = new LinkedHashMap<>();
    private final Map<Class<?>, FxParameterEditorBuilder<?>> buildersByType = new LinkedHashMap<>();


    /**
     * Registers a parameter editor builder for the specified parameter
     * @param parameterName the name of the parameter for which the editor builder is registered
     * @param builder the parameter editor builder to be registered
     * @return the parameter editor builder previously registered for the specified parameter name
     */
    public FxParameterEditorBuilder<?> registerBuilderForParameter(String parameterName, FxParameterEditorBuilder<?> builder) {
        return buildersByParameter.put(parameterName, builder);
    }

    /**
     * Registers a parameter editor builder for parameters with the specified type
     * @param type the type of the parameters for which the editor builder is registered
     * @param builder the parameter editor builder to be registered
     * @return the parameter editor builder previously registered for the specified parameter type
     */
    public FxParameterEditorBuilder<?> registerBuilderForType(Class<?> type, FxParameterEditorBuilder<?> builder) {
        return buildersByType.put(type, builder);
    }

    /**
     * Retrieves an appropriate parameter editor builder for the specified parameter
     * @param parameter the parameter for which an editor builder should be retrieved
     * @return the editor builder appropriate for the specified parameter
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T, E extends ParameterEditor<T>, P> ParameterEditorBuilder<T, E, P> getBuilder(Parameter<T> parameter) {
        ParameterEditorBuilder builder = buildersByParameter.get(parameter.getName());
        if(builder == null) {
            Class<T> valueClass = parameter.getValueClass();
            builder = buildersByType.get(valueClass);
            if(builder == null) {
                Class currentClass = null;
                for(Class cls : buildersByType.keySet()) {
                    if(cls.isAssignableFrom(valueClass)) {
                        if((currentClass == null) || (currentClass.isAssignableFrom(cls))) {
                            builder = buildersByType.get(cls);
                            currentClass = cls;
                        }
                    }
                }
            }
        }
        if(builder == null) {
            if(parameter.isIterable()) {
                builder = new ComboBoxParameterEditor.Builder<>();
            } else {
                builder = new TextFieldParameterEditor.Builder<>();
            }
        }
        return builder;
    }
}
