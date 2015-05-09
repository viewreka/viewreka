package org.beryx.viewreka.fxui.editor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.project.ParameterEditor;
import org.beryx.viewreka.project.ParameterEditorBuilder;

public class ParameterEditorManager {
	private final Map<String, FxParameterEditorBuilder<?>> buildersByParameter = new LinkedHashMap<>();
	private final Map<Class<?>, FxParameterEditorBuilder<?>> buildersByType = new LinkedHashMap<>();


	public FxParameterEditorBuilder<?> registerBuilderForParameter(String parameterName, FxParameterEditorBuilder<?> builder) {
		return buildersByParameter.put(parameterName, builder);
	}

	public FxParameterEditorBuilder<?> registerBuilderForType(Class<?> type, FxParameterEditorBuilder<?> builder) {
		return buildersByType.put(type, builder);
	}

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
