package org.beryx.viewreka.settings;

/**
 * The interface required for the settings handled by a {@link SettingsManager}.
 */
public interface Settings {
    /**
     * A {@link SettingsManager} calls this method after loading the settings.
     * Subclasses may override this default empty implementation.
     */
    default void afterLoad() {}

    /**
     * A {@link SettingsManager} calls this method before saving the settings.
     * Subclasses may override this default empty implementation.
     */
    default void beforeSave() {}
}
