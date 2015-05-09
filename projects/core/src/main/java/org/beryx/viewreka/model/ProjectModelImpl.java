package org.beryx.viewreka.model;

import static org.beryx.viewreka.core.Util.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProjectModelImpl<V extends ViewModel> implements ProjectModel<V> {
	private String name;
	private String title = "";
	private String description = "";

	private final List<V> views = new ArrayList<>();
	private final Map<String, DataSource<? extends Query>> dataSources = new LinkedHashMap<>();

	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public List<V> getViews() {
		return views;
	}

	@Override
	public Map<String, DataSource<? extends Query>> getDataSources() {
		return dataSources;
	}

	public void validate() {
		requireNonNull(name, "name");
		requireNonNull(description, "description");
	}

	@Override
	public String toString() {
		return name + ((description == null || description.isEmpty()) ? "" : (" (" + description + ")"));
	}
}
