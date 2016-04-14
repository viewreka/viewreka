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
package org.beryx.viewreka.fxui.settings;

import static org.beryx.viewreka.core.Util.getValue;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.beryx.viewreka.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * General GUI settings (not related to a specific Viewreka project).
 */
public interface GuiSettings extends Settings {
	static final Logger _log = LoggerFactory.getLogger(GuiSettings.class);

	/**
	 * @return a map of property values indexed by their names
	 */
	Map<String, Serializable> getProperties();

	/**
	 * Retrieves the value of a specified property or a default value.
	 * @param name the name of the desired property
	 * @param defaultValue the default value to be returned if the specified property is not available
	 * @param allowNull true, if null is a permitted value for the specified property
	 * @return  the value of the property with the specified name or a default value if no such property exists.
	 * The default value is also returned if the value of the specified property is null and {@code allowNull} is false.
	 */
	default <T> T getProperty(String name, T defaultValue, boolean allowNull) {
		return getValue(getProperties(), name, defaultValue, allowNull);
	}

	/**
	 * Sets the value of a specified property.
	 * @param name the name of the property to be set
	 * @param value the value to be set
	 * @return the previous value of the property with the specified name
	 */
	default <T extends Serializable> Object setProperty(String name, T value) {
        _log.debug("Setting {} to {}", name, value);
		return getProperties().put(name, value);
	}

	/**
	 * @return the X coordinate of the upper-left corner of the application window
	 */
	double getWindowX();

	/**
	 * @param x the X coordinate of the upper-left corner of the application window
	 */
	void setWindowX(double x);

	/**
	 * @return the Y coordinate of the upper-left corner of the application window
	 */
	double getWindowY();

	/**
	 * @param y the Y coordinate of the upper-left corner of the application window
	 */
	void setWindowY(double y);

	/**
	 * @return the width of the application window
	 */
	double getWindowWidth();

	/**
	 * @param width the width of the application window
	 */
	void setWindowWidth(double width);

	/**
	 * @return the height of the application window
	 */
	double getWindowHeight();

	/**
	 * @param height the height of the application window
	 */
	void setWindowHeight(double height);

	/**
	 * @return the list of the file paths of recently opened Viewreka project script files
	 */
	List<String> getRecentProjectPaths();

	/**
	 * @return the directory of the script file of the most recently opened Viewreka project.
	 * If not available, the current user directory is returned.
	 */
	default File getMostRecentProjectDir() {
		List<String> recentProjectPaths = getRecentProjectPaths();
		File prjDir = null;
		try {
			if(!recentProjectPaths.isEmpty()) {
				File prj = new File(recentProjectPaths.get(0));
				prjDir = prj.getParentFile();
			}
			if((prjDir == null) || !prjDir.isDirectory()) {
				String defaultDirPath = System.getProperty("user.dir");
				prjDir = new File(defaultDirPath);
			}
		} catch(Exception e) {
			_log.warn("Cannot retrieve most recent project dir", e);
			prjDir = null;
		}
		return (prjDir != null && prjDir.isDirectory()) ? prjDir : new File(".");
	}

	/**
	 * @return the maximum number of entries in the list returned by {@link #getRecentProjectPaths()}
	 */
	int getMaxRecentProjects();

	/**
	 * @param maxRecentProjects the maximum number of entries in the list returned by {@link #getRecentProjectPaths()}
	 */
	void setMaxRecentProjects(int maxRecentProjects);

	/**
	 * @return true, if on start the application should try to open the last opened project
	 */
	boolean isLoadLastProject();

	/**
	 * @param loadLastProject true, if on start the application should try to open the last opened project
	 */
	void setLoadLastProject(boolean loadLastProject);
}
