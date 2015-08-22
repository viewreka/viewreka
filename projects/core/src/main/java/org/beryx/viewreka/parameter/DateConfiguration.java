package org.beryx.viewreka.parameter;

import java.text.DateFormat;
import java.util.Locale;

/**
 * A configuration interface typically associated with date parameters.
 * @param <T> the date value type
 */
public interface DateConfiguration<T> {
    /**
     * @return the locale of this configuration
     */
    Locale getLocale();

    /**
     * @return the date format used by this configuration
     */
    DateFormat getDateFormat();

    /**
     * @return a string indicating the pattern used by {@link #getDateFormat()}
     */
    String getDatePattern();

    /**
     * Converts the {@code millis} to the date value type {@code T}.
     * @param millis the value to be converted (in milliseconds)
     * @return the corresponding value of type {@code T}
     */
    T fromMilliseconds(long millis);
}