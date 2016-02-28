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

import javafx.stage.Screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the {@link GuiSettings} interface.
 */
public class GuiSettingsImpl implements GuiSettings {
    private static final double DEFAULT_X = 10;
    private static final double DEFAULT_Y = 10;
	private double x = DEFAULT_X;
	private double y = DEFAULT_Y;
	private double width = 1280;
	private double height = 800;
	private int maxRecentProjects = 10;
	private boolean loadLastProject = true;

	private List<String> recentProjectPaths = new ArrayList<>();

	private Map<String, Serializable> properties = new LinkedHashMap<>();

    @Override
    public void afterLoad() {
        if(Screen.getScreensForRectangle(x, y, x+1, y+1).isEmpty()) {
            x = DEFAULT_X;
            y = DEFAULT_Y;
        }
    }

    @Override
	public double getWindowX() {
		return x;
	}

	@Override
	public void setWindowX(double x) {
		this.x = x;
	}

	@Override
	public double getWindowY() {
		return y;
	}

	@Override
	public void setWindowY(double y) {
		this.y = y;
	}

	@Override
	public double getWindowWidth() {
		return width;
	}

	@Override
	public void setWindowWidth(double width) {
		this.width = width;
	}

	@Override
	public double getWindowHeight() {
		return height;
	}

	@Override
	public void setWindowHeight(double height) {
		this.height = height;
	}

	@Override
	public List<String> getRecentProjectPaths() {
		return recentProjectPaths;
	}

	/**
	 * @param recentProjectPaths the list of the file paths of recently opened Viewreka project script files
	 */
	public void setRecentProjectPaths(List<String> recentProjectPaths) {
		this.recentProjectPaths = recentProjectPaths;
	}

	@Override
	public int getMaxRecentProjects() {
		return maxRecentProjects;
	}

	@Override
	public void setMaxRecentProjects(int maxRecentProjects) {
		this.maxRecentProjects = maxRecentProjects;
	}

	@Override
	public boolean isLoadLastProject() {
		return loadLastProject;
	}

	@Override
	public void setLoadLastProject(boolean loadLastProject) {
		this.loadLastProject = loadLastProject;
	}

	@Override
	public Map<String, Serializable> getProperties() {
		return properties;
	}

	/**
	 * @param properties the map of property values indexed by their names
	 */
	public void setProperties(Map<String, Serializable> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(256);
		sb.append("{");
		sb.append("(x,y): (").append(x).append(",").append(y).append("), (width,height): (").append(width).append(",").append(height).append(")");
		sb.append(", recentProjectPaths: ").append(recentProjectPaths);
		sb.append(", maxRecentProjects: ").append(maxRecentProjects);
		sb.append(", loadLastProject: ").append(loadLastProject);
		sb.append(", properties: ").append(properties);
		sb.append("}");
		return sb.toString();
	}
}
