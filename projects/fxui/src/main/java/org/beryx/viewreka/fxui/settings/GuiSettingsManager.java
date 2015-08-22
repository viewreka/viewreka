package org.beryx.viewreka.fxui.settings;

import org.beryx.viewreka.settings.SettingsManagerImpl;

/**
 * An extension of {@link SettingsManagerImpl} for handling GUI settings.
 */
public class GuiSettingsManager extends SettingsManagerImpl<GuiSettings> {

	public GuiSettingsManager() {
		super(getSettingsDirPath(), getSettingsFileName());
	}

	private static String getSettingsDirPath() {
		String dirPath = System.getProperty("viewreka.settings.dir");
		if(dirPath == null) {
			dirPath = System.getProperty("user.home") + "/.viewreka";
		}
		return dirPath;
	}

	private static String getSettingsFileName() {
		return System.getProperty("viewreka.settings.file", "config.xml");
	}

	@Override
	protected GuiSettings createNewSettings() {
		return new GuiSettingsImpl();
	}

}
