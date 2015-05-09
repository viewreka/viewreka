package org.beryx.viewreka.dsl.parameter

import groovy.transform.InheritConstructors;
import groovy.transform.ToString;

import java.util.Locale;
import java.util.Map;

import org.beryx.viewreka.model.DatasetProvider;

@ToString(includePackage=false)
class DateParameterDelegate<T> extends ParameterDelegate<T> {
	Locale locale
	String datePattern

	public DateParameterDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
		super(name, dataSetProviders)
	}
}
