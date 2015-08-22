package org.beryx.viewreka.settings;

/**
 * A concrete settings manager implementation for handling project settings.
 */
public class ProjectSettingsManager extends SettingsManagerImpl<ProjectSettings> {

    /**
     * Constructs a ProjectSettingsManager that stores the settings in a specified file.
     * @param settingsDirPath the path to the directory of the file used to store the project settings
     * @param settingsFileName the name of the file used to store the project settings
     */
    public ProjectSettingsManager(String settingsDirPath, String settingsFileName) {
        super(settingsDirPath, settingsFileName);
    }

    @Override
    protected ProjectSettings createNewSettings() {
        return new ProjectSettingsImpl();
    }

}
