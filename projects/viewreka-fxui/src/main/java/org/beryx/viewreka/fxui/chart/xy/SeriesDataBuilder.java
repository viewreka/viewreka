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

import javafx.scene.chart.XYChart.Data;

/**
 * A builder used to create series data as well as the X and Y axes.
 * @param <X> the type of the X axis
 * @param <Y> the type of the Y axis
 */
public interface SeriesDataBuilder<X,Y> {
	/**
	 * @return a builder for the X axis
	 */
	AxisBuilder<?, X> getXAxisBuilder();

	/**
	 * @return a builder for the Y axis
	 */
	AxisBuilder<?, Y> getYAxisBuilder();

	/**
	 * Creates series data for the specified configuration
	 * @param seriesConfig the chart series configuration for which series data should be created
	 * @return the series data
	 */
	Collection<Data<X, Y>> createSeriesData(SeriesConfig<X,Y> seriesConfig);
}
