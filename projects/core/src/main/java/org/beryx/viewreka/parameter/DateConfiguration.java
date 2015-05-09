package org.beryx.viewreka.parameter;

import java.text.DateFormat;
import java.util.Locale;

public interface DateConfiguration<T> {
	Locale getLocale();
	DateFormat getDateFormat();
	String getDatePattern();
	T fromMilliseconds(long millis);
}