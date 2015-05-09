package org.beryx.viewreka.model;

import java.util.List;
import java.util.Map;

public interface ProjectModel<V extends ViewModel> {
	String getName();
	String getTitle();
	String getDescription();
	List<V> getViews();
	Map<String, DataSource<? extends Query>> getDataSources();
}
