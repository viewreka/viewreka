package org.beryx.viewreka.settings;

import java.util.function.Consumer;

/** A settings manager is responsible for retrieving and saving settings, as well as for notifying registered listeners about the creation of new settings.
 * @param <T> the type of the settings structure handled by this manager
 */
public interface SettingsManager<T> {
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
