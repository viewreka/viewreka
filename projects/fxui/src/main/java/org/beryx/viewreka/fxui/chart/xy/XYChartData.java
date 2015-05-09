package org.beryx.viewreka.fxui.chart.xy;

import java.util.Collection;
import java.util.Map;

import javafx.scene.chart.XYChart.Data;

public class XYChartData<X,Y> {
	private final String title;
	/** indexed by seriesName */
	private final Map<String, Collection<Data<X, Y>>> dataMap;

	public XYChartData(String title, Map<String, Collection<Data<X, Y>>> dataMap) {
		this.title = title;
		this.dataMap = dataMap;
	}

	public String getTitle() {
		return title;
	}

	/** indexed by seriesName */
	public Map<String, Collection<Data<X, Y>>> getDataMap() {
		return dataMap;
	}

	@Override
	public String toString() {
		return title + ": " + dataMap;
	}
}
