package org.beryx.viewreka.fxui.settings;

import static org.beryx.viewreka.core.Util.getValue;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface GuiSettings {
	static final Logger _log = LoggerFactory.getLogger(GuiSettings.class);

	Map<String, Serializable> getProperties();

	default <T> T getProperty(String name, T defaultValue, boolean allowNull) {
		return getValue(getProperties(), name, defaultValue, allowNull);
	}

	default <T extends Serializable> Object setProperty(String name, T value) {
		return getProperties().put(name, value);
	}

	double getWindowX();
	void setWindowX(double x);

	double getWindowY();
	void setWindowY(double y);

	double getWindowWidth();
	void setWindowWidth(double width);

	double getWindowHeight();
	void setWindowHeight(double height);

	List<String> getRecentProjectPaths();

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

	int getMaxRecentProjects();
	void setMaxRecentProjects(int maxRecentProjects);

	boolean isLoadLastProject();
	void setLoadLastProject(boolean loadLastProject);
}
