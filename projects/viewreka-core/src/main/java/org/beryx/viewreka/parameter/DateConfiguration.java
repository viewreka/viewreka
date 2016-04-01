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