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
