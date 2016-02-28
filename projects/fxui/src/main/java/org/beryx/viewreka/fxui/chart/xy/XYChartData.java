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
