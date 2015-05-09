package org.beryx.viewreka.settings;

import java.util.function.Consumer;

public interface SettingsManager<T> {
	T getSettings();
	void saveSettings();
	public boolean addCreationListener(Consumer<T> listener);
	public boolean removeCreationListener(Consumer<T> listener);
}
