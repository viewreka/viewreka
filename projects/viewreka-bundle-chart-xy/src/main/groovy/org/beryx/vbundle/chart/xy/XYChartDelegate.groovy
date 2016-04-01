/*
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
package org.beryx.vbundle.chart.xy

import groovy.transform.ToString
import javafx.scene.chart.AreaChart
import javafx.scene.chart.Axis
import javafx.scene.chart.BarChart
import javafx.scene.chart.BubbleChart
import javafx.scene.chart.LineChart
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.StackedAreaChart
import javafx.scene.chart.StackedBarChart
import javafx.scene.chart.XYChart

import org.beryx.viewreka.core.ViewrekaException
import org.beryx.viewreka.dsl.BaseDelegate
import org.beryx.viewreka.fxui.chart.xy.SeriesConfig
import org.beryx.viewreka.fxui.chart.xy.XYChartBuilder
import org.beryx.viewreka.fxui.chart.xy.XYChartCreator
import org.beryx.viewreka.model.DatasetProvider

/**
 * The closure delegate used by a {@link XYChartBuilderBuilder}.
 */
@ToString
class XYChartDelegate extends BaseDelegate {
	final String name
	DatasetProvider dataset

	private final XYChartBuilder xyChartBuilder

	public XYChartDelegate(String name, Map<String, DatasetProvider> dataSetProviders, XYChartBuilder xyChartBuilder) {
		this.name = name
		injectProperties(dataSetProviders)

		this.xyChartBuilder = xyChartBuilder
		this.xyChartBuilder.seriesDataBuilder = new SeriesDataBuilderImpl<>()

		injectCreator('area', {xAxis, yAxis -> new AreaChart<>(xAxis, yAxis)})
		injectCreator('bar', {xAxis, yAxis -> new BarChart<>(xAxis, yAxis)})
		injectCreator('bubble', {xAxis, yAxis -> new BubbleChart<>(xAxis, yAxis)})
		injectCreator('line', {xAxis, yAxis -> new LineChart<>(xAxis, yAxis)})
		injectCreator('scatter', {xAxis, yAxis -> new ScatterChart<>(xAxis, yAxis)})
		injectCreator('stackedArea', {xAxis, yAxis -> new StackedAreaChart<>(xAxis, yAxis)})
		injectCreator('stackedBar', {xAxis, yAxis -> new StackedBarChart<>(xAxis, yAxis)})

		// default styles:
		styles(this.'line')

	}


	private void injectCreator(String style, Closure<XYChart> closure) {
		XYChartCreator creator = new XYChartCreator(style) {
			XYChart createChart(Axis xAxis, Axis yAxis) {
				return closure.call(xAxis, yAxis)
			}
		}
		injectProperty(style, creator)
	}

	def styles(XYChartCreator... chartCreators) {
		xyChartBuilder.chartCreators.clear()
		chartCreators.each { creator -> xyChartBuilder.chartCreators[creator.style] = creator }
	}

	def xAxis(Map options) {
		xyChartBuilder.seriesDataBuilder.xAxisBuilder = new AxisBuilderImpl(options)
	}

	def yAxis(Map options) {
		xyChartBuilder.seriesDataBuilder.yAxisBuilder = new AxisBuilderImpl(options)
	}

	def series(Map options) {
		options.each {String seriesName, Collection columns ->
			SeriesConfig cfg = xyChartBuilder.seriesConfigMap[seriesName]
			if(!cfg) {
				cfg = new SeriesConfigImpl();
				xyChartBuilder.seriesConfigMap[seriesName] = cfg
			}

			if(columns.size() < 2) throw new ViewrekaException("Series $seriesName: Expected at least 2 columns.")
			cfg.XColumn = getInt(columns[0])
			cfg.XColumnName = "${columns[0]}"
			cfg.YColumn = getInt(columns[1])
			cfg.YColumnName = "${columns[1]}"
			if(columns.size() > 2) {
				cfg.extraColumn = getInt(columns[2])
				cfg.extraColumnName = "${columns[2]}"
			}
		}
	}

	private int getInt(value) {
		return (value instanceof Number) ? ((Number)value).intValue() : -1
	}
}
