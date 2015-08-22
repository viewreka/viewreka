package org.beryx.viewreka.fxui.chart.xy;

import java.util.Collection;
import java.util.Map;

import javafx.scene.chart.XYChart.Data;

/**
 * The chart data handled by a {@link XYChartController}
 * @param <X> the type of the X axis
 * @param <Y> the type of the Y axis
 */
public class XYChartData<X,Y> {
	private final String title;
	/** indexed by seriesName */
	private final Map<String, Collection<Data<X, Y>>> dataMap;

	/**
	 * @param title the chart title
	 * @param dataMap the map of series data indexed by series names
	 */
	public XYChartData(String title, Map<String, Collection<Data<X, Y>>> dataMap) {
		this.title = title;
		this.dataMap = dataMap;
	}

	/**
	 * @return the chart title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Retrieves the map of series data
	 * @return a map of series data indexed by series names
	 */
	public Map<String, Collection<Data<X, Y>>> getDataMap() {
		return dataMap;
	}

	@Override
	public String toString() {
		return title + ": " + dataMap;
	}
}
