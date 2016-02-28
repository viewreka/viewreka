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

import groovy.util.logging.Slf4j
import javafx.scene.chart.XYChart.Data

import org.beryx.viewreka.fxui.chart.xy.AxisBuilder
import org.beryx.viewreka.fxui.chart.xy.SeriesConfig
import org.beryx.viewreka.fxui.chart.xy.SeriesDataBuilder
import org.beryx.viewreka.model.Dataset
import org.beryx.viewreka.parameter.Parameter

/**
 * Implementation of the {@link SeriesDataBuilder} interface.
 */
@Slf4j
class SeriesDataBuilderImpl<X,Y> implements SeriesDataBuilder<X,Y> {
	AxisBuilder<?, X> xAxisBuilder
	AxisBuilder<?, Y> yAxisBuilder

	@Override
	public AxisBuilder<?, X> getXAxisBuilder() {
		return xAxisBuilder;
	}

	@Override
	public AxisBuilder<?, Y> getYAxisBuilder() {
		return yAxisBuilder;
	}

	@Override
	public Collection<Data<X, Y>> createSeriesData(SeriesConfig<X, Y> cfg) {
		List<Data<X, Y>> seriesData = new ArrayList<>()
		Collection<String> chartParameterValues = (cfg.chartParameter == null) ? [] :
				cfg.chartParameter.possibleValues.collect{val -> cfg.chartParameter.asString(val)};

		if(chartParameterValues.size > 0) {
			for(String chartParameterValue : chartParameterValues) {
				cfg.chartParameter.setValueFromString(chartParameterValue)
				updateSeriesData(seriesData, cfg)
			}
		} else {
			updateSeriesData(seriesData, cfg)
		}
		return seriesData
	}

	private void updateSeriesData(List<Data<X, Y>> seriesData, SeriesConfig<X, Y> cfg) {
		Class xQueryResultClass = cfg.XColumnType ?: xAxisBuilder.getDefaultQueryResultType()
		Class yQueryResultClass = cfg.YColumnType ?: yAxisBuilder.getDefaultQueryResultType()
		Dataset dataset = cfg.datasetProvider.dataset
		int rowCount = dataset.getRowCount();
		for(int row=0; row<rowCount; row++) {
			Object xQueryVal = (cfg.XColumn < 0)
					? dataset.getValue(row, cfg.XColumnName, xQueryResultClass)
					: dataset.getValue(row, cfg.XColumn, xQueryResultClass)
			if(xQueryVal == null) {
				log.error("xQueryVal is null for row $row, column ${cfg.XColumn} of dataset: $dataset");
				continue;
			}
			X xVal = cfg.XDataConverter ? cfg.XDataConverter.apply(xQueryVal) : xAxisBuilder?.defaultDataConverter.apply(xQueryVal)

			Object yQueryVal = (cfg.YColumn < 0)
					? dataset.getValue(row, cfg.YColumnName, yQueryResultClass)
					: dataset.getValue(row, cfg.YColumn, yQueryResultClass)
			if(yQueryVal == null) {
				log.error("yQueryVal is null for row $row, column ${cfg.YColumn} of dataset: $dataset");
				continue;
			}
			Y yVal = cfg.YDataConverter ? cfg.YDataConverter.apply(yQueryVal) : yAxisBuilder?.defaultDataConverter.apply(yQueryVal)


			// TODO - extraConverter
			Object extraVal = (cfg.extraColumn >= 0)
				? dataset.getObject(row, cfg.extraColumn)
				: (cfg.extraColumnName == null)
				? null
				: dataset.getObject(row, cfg.extraColumnName);
			if(extraVal instanceof Number) {
				extraVal = 100.0 * ((Number)extraVal).doubleValue();
			}

			Data<X,Y> data = new Data<>(xVal, yVal, extraVal);
			seriesData.add(data)
		}
	}

}
