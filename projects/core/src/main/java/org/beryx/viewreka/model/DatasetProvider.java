package org.beryx.viewreka.model;

import java.util.function.Consumer;


public interface DatasetProvider extends Parameterized, AutoCloseable {
	String getName();
	Dataset getDataset();
	void setDirty();
	boolean addDirtyListener(Consumer<DatasetProvider> listener);
	boolean removeDirtyListener(Consumer<DatasetProvider> listener);
}
