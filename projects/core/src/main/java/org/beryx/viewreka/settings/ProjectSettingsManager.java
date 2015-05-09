package org.beryx.viewreka.settings;

public class ProjectSettingsManager extends SettingsManagerImpl<ProjectSettings> {

	public ProjectSettingsManager(String settingsDirPath, String settingsFileName) {
		super(settingsDirPath, settingsFileName);
	}

	@Override
	protected ProjectSettings createNewSettings() {
		return new ProjectSettingsImpl();
	}

}
