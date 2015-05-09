package org.beryx.viewreka.settings;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SettingsManagerImpl<T> implements SettingsManager<T> {
	private static final Logger log = LoggerFactory.getLogger(SettingsManagerImpl.class);

	private final String settingsDirPath;
	private final String settingsFileName;

	private T settings = null;

	private final List<Consumer<T>> creationListeners = new ArrayList<>();

	protected abstract T createNewSettings();

	public SettingsManagerImpl(String settingsDirPath, String settingsFileName) {
		this.settingsDirPath = (settingsDirPath == null) ? "" : settingsDirPath;
		this.settingsFileName = (settingsFileName == null) ? "" : settingsFileName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getSettings() {
		if(settings == null) {
			if(!settingsFileName.isEmpty()) {
				Path dirPath = Paths.get(settingsDirPath);
				Path filePath = Paths.get(dirPath.toString(), settingsFileName);
				try (XMLDecoder decoder = new XMLDecoder(new FileInputStream(filePath.toFile()))) {
					settings = (T)decoder.readObject();
					if(settings == null) {
						log.warn("Null existing settings. Creating new settings...");
					}
				} catch(Exception e) {
					if(e instanceof FileNotFoundException) {
						log.debug("No existing settings found. Creating new settings...");
					} else {
						log.warn("Failed to read existing settings. Creating new settings...", e);
					}
					settings = null;
				}
			}
			if(settings == null) {
				settings = createNewSettings();
				creationListeners.forEach(listener -> listener.accept(settings));
			}
		}
		return settings;
	}

	@Override
	public void saveSettings() {
		if(settingsFileName.isEmpty()) return;
		Path dirPath = Paths.get(settingsDirPath);
		try (XMLEncoder encoder = new XMLEncoder(new FileOutputStream(Paths.get(Files.createDirectories(dirPath).toString(), settingsFileName).toFile()))) {
			encoder.writeObject(settings);
		} catch(Exception e) {
			log.error("Cannot save settings.", e);
		}
	}

	@Override
	public boolean addCreationListener(Consumer<T> listener) {
		return creationListeners.add(listener);
	}

	@Override
	public boolean removeCreationListener(Consumer<T> listener) {
		return creationListeners.remove(listener);
	}
}
