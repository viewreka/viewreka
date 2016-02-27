package org.beryx.viewreka.fxui.settings;

import org.beryx.viewreka.settings.SettingsManagerImpl;

/**
 * An extension of {@link SettingsManagerImpl} for handling GUI settings.
 */
public class GuiSettingsManager extends SettingsManagerImpl<GuiSettings> {
    public static final String SETTINGS_DIR_PROPERTY = "viewreka.settings.dir";
    public static final String SETTINGS_FILE_PROPERTY = "viewreka.settings.file";
    public static final String DEFAULT_SETTINGS_FILE_NAME = "config.xml";

	public GuiSettingsManager() {
		super(getSettingsDirPath(), getSettingsFileName());
	}

	private static String getSettingsDirPath() {
		String dirPath = System.getProperty(SETTINGS_DIR_PROPERTY);
		if(dirPath == null) {
			dirPath = System.getProperty("user.home") + "/.viewreka";
		}
		return dirPath;
	}

	private static String getSettingsFileName() {
		return System.getProperty(SETTINGS_FILE_PROPERTY, DEFAULT_SETTINGS_FILE_NAME);
	}

	@Override
	protected GuiSettings createNewSettings() {
		return new GuiSettingsImpl();
	}

}
