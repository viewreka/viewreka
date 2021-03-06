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

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import org.beryx.viewreka.chart.ChartController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A controller for an XY chart
 * @param <X> the type of the X axis
 * @param <Y> the type of the Y axis
 */
public class XYChartController<X, Y> implements ChartController<XYChartData<X,Y>> {
	private static final Logger log = LoggerFactory.getLogger(XYChartController.class);

	private final Map<String, SeriesConfig<X, Y>> seriesConfigMap;
	private final SeriesDataBuilder<X, Y> seriesDataBuilder;
	private final Map<String, Series<X, Y>> seriesMap = new LinkedHashMap<>();
	private final Supplier<String> titleSupplier;
	private final Supplier<String> stylesheetSupplier;

	private final StackPane stackPane = new StackPane();
	private final XYChart<X, Y> chart;

	/**
	 * @param chartParentPane the parent pane of the chart handled by this controller
	 * @param seriesConfigMap the map of series configurations
	 * @param seriesDataBuilder the series data builder
	 * @param chartCreator the builder responsible for creating the chart handled by this controller
	 * @param titleSupplier the supplier of the chart title
	 * @param stylesheetSupplier the supplier of the stylesheet used by the chart handled by this controller
	 */
	public XYChartController(Pane chartParentPane, Map<String, SeriesConfig<X, Y>> seriesConfigMap,
			SeriesDataBuilder<X, Y> seriesDataBuilder, XYChartCreator chartCreator, Supplier<String> titleSupplier, Supplier<String> stylesheetSupplier) {
		this.seriesConfigMap = seriesConfigMap;
		this.seriesDataBuilder = seriesDataBuilder;
		this.titleSupplier = titleSupplier;
		this.stylesheetSupplier = stylesheetSupplier;

		Axis<X> xAxis = seriesDataBuilder.getXAxisBuilder().get();
		Axis<Y> yAxis = seriesDataBuilder.getYAxisBuilder().get();

		AnchorPane.setBottomAnchor(stackPane, 5.0);
	    AnchorPane.setRightAnchor(stackPane, 5.0);
	    AnchorPane.setTopAnchor(stackPane, 5.0);
	    AnchorPane.setLeftAnchor(stackPane, 5.0);
		chartParentPane.getChildren().add(stackPane);

		chart = chartCreator.createChart(xAxis, yAxis);
		chart.setAnimated(false);

		seriesConfigMap.keySet().forEach(seriesName -> {
			Series<X,Y> series = new Series<>();
			series.setName(seriesName);
			seriesMap.put(seriesName, series);
			chart.getData().add(series);
		});

		stackPane.getChildren().add(chart);

	}

	@Override
	public XYChartData<X,Y> createChartData() {
		long start = System.currentTimeMillis();

		String title = titleSupplier.get();
		Map<String, Collection<Data<X, Y>>> dataMap = new LinkedHashMap<>();
		XYChartData<X,Y> xyChartData = new XYChartData<>(title, dataMap);

		seriesConfigMap.forEach((seriesName, seriesConfig) -> {
			Collection<Data<X,Y>> seriesData = seriesDataBuilder.createSeriesData(seriesConfig);
			dataMap.put(seriesName, seriesData);
		});

		log.debug("XYChartData created in {} ms.", System.currentTimeMillis() - start);

		return xyChartData;
	}


	@Override
	public void displayChart(XYChartData<X,Y> data) {
		if(data == null) {
			data = new XYChartData<>("", Collections.emptyMap());
		}
		long start = System.currentTimeMillis();
		log.debug("Displaying XYChart with {} series...", data.getDataMap().size());

		if(log.isTraceEnabled()) {
			Collection<Data<X, Y>> currData = data.getDataMap().values().iterator().next();
			int size = currData.size();
			log.trace(currData.stream().map(d -> d.toString()).collect(Collectors.joining("\n\t", "Series with " + size + " elements:\n\t", "\n------------------------\n\n")));
		}

		if(stylesheetSupplier != null) {
			chart.getStylesheets().clear();
			String stylesheet = stylesheetSupplier.get();
			if(stylesheet != null) {
				// Dirty hack: Using a copy of the original CSS files, because jdk.1.8.0_40 seems to ignore changes in a previously added CSS file.
				try (InputStream sourceStream = new URI(stylesheet).toURL().openStream()) {
					File cssFile = File.createTempFile("viewreka-", ".css");
					Path targetCSS = Paths.get(cssFile.toURI().normalize());
					Files.copy(sourceStream, targetCSS, StandardCopyOption.REPLACE_EXISTING);
					chart.getStylesheets().add(targetCSS.toUri().normalize().toString());
				} catch(Exception e) {
					log.warn("Cannot configure the cloned CSS stylesheet.", e);
					chart.getStylesheets().add(stylesheet);
				}
			}
		}

		chart.setTitle(data.getTitle());

		Map<String, Collection<Data<X, Y>>> dataMap = data.getDataMap();
		seriesMap.forEach((seriesName, series) -> {
			series.getData().clear();
			Collection<Data<X, Y>> dataCollection = dataMap.get(seriesName);
			if(dataCollection != null) {
				series.getData().addAll(dataCollection);
			}
		});

		log.debug("XYChart displayed in {} ms.", System.currentTimeMillis() - start);
	}
}
