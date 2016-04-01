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

import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;

/**
 * Abstract builder for creating XY charts.
 */
public abstract class XYChartCreator {
	private final String style;

	/**
	 * Creates an XY chart for the specified X and Y axes.
	 * @param xAxis the X axis
	 * @param yAxis the Y axis
	 * @return the newly created XY chart
	 */
	public abstract <X, Y> XYChart<X, Y> createChart(Axis<X> xAxis, Axis<Y> yAxis);

	/**
	 * @param style a string identifying the style of the charts created by this builder (for example: "line", "bar", "stackedArea" etc.)
	 */
	public XYChartCreator(String style) {
		this.style = style;
	}

	/**
	 * @return the style of of the charts created by this builder (for example: "line", "bar", "stackedArea" etc.)
	 */
	public String getStyle() {
		return style;
	}

}
