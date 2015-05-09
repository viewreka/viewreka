package org.beryx.viewreka.fxui.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GuiSettingsImpl implements GuiSettings {
	private double x = 10;
	private double y = 10;
	private double width = 1280;
	private double height = 800;
	private int maxRecentProjects = 10;
	private boolean loadLastProject = true;

	private List<String> recentProjectPaths = new ArrayList<>();

	private Map<String, Serializable> properties = new LinkedHashMap<>();

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