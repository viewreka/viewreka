package org.beryx.vbundle.parameter

import groovy.transform.ToString
import org.beryx.viewreka.dsl.parameter.ParameterDelegate
import org.beryx.viewreka.model.DatasetProvider
/**
 * The closure delegate used by date/time {@link ParameterBuilder}s (such as {@link SqlDateParameterBuilder}, {@link SqlTimeParameterBuilder}, {@link SqlTimestampParameterBuilder}).
 */
@ToString(includePackage=false)
class DateParameterDelegate<T> extends ParameterDelegate<T> {
    Locale locale
    String datePattern

    public DateParameterDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
        super(name, dataSetProviders)
    }
}
