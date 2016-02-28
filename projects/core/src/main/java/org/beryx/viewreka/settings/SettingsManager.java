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
package org.beryx.viewreka.settings;

import java.util.function.Consumer;

/** A settings manager is responsible for retrieving and saving settings, as well as for notifying registered listeners about the creation of new settings.
 * @param <T> the type of the settings structure handled by this manager
 */
public interface SettingsManager<T extends Settings> {
    /**
     * Retrieves the settings handled by this manager.
     * @return the settings handled by this manager
     */
    T getSettings();

    /**
     * Saves the settings handled by this manager.
     */
    void saveSettings();

    /**
     * Adds a new creation listener.
     * @param listener the creation listener, which will be notified about the creation of new settings
     * @return true, if the creation listener has been successfully added.
     */
    boolean addCreationListener(Consumer<T> listener);


    /**
     * Removes a creation listener.
     * @param listener the creation listener to be removed
     * @return true, if the specified creation listener has been found and successfully removed
     */
    boolean removeCreationListener(Consumer<T> listener);
}
