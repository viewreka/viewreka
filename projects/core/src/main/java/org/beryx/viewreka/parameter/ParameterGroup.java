package org.beryx.viewreka.parameter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.model.Parameterized;

public class ParameterGroup implements Parameterized {
	private final Map<String, Parameter<?>> parameterMap = new LinkedHashMap<>();

	@Override
	public Set<String> getParameterNames() {
		return Collections.unmodifiableSet(parameterMap.keySet());
	}

	public Collection<Parameter<?>> getParameters() {
		return Collections.unmodifiableCollection(parameterMap.values());
	}

	public Parameter<?> getParameter(String name) {
		return parameterMap.get(name);
	}

	public int getParameterIndex(String name) {
		return Arrays.binarySearch(parameterMap.keySet().toArray(), name);
	}

	public Parameter<?> addParameter(Parameter<?> parameter) {
		Parameter<?> oldParameter = parameterMap.put(parameter.getName(), parameter);
		if(oldParameter != null) {
			throw new ViewrekaException("Parameter " + parameter.getName() + " already defined.");
		}
		for(Parameter<?> p : parameterMap.values()) {
			p.parameterGroupChanged();
		}
		return oldParameter;
	}

	@Override
	public String toString() {
		return parameterMap.toString();
	}
}
