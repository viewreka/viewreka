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
package org.beryx.vbundle.chart.xy;

import groovy.transform.ToString

import java.util.function.Function

import org.beryx.viewreka.fxui.chart.xy.AxisBuilder
import org.beryx.viewreka.fxui.chart.xy.SeriesConfig
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.Parameter

/**
 * Implementation of the {@link SeriesConfig} interface.
 */
@ToString
class SeriesConfigImpl<X, Y> implements SeriesConfig<X, Y> {
	DatasetProvider datasetProvider
	int XColumn = -1
	String XColumnName
	int YColumn = -1
	String YColumnName
	int extraColumn = -1
	String extraColumnName
	Parameter<?> chartParameter
	Class<?> XColumnType
	Class<?> YColumnType
	Function<?, X> XDataConverter
	Function<?, Y> YDataConverter

	public void configureDefaultsFrom(Parameter<?> chartParameter, DatasetProvider datasetProvider, AxisBuilder<?, Y> xAxisBuilder, AxisBuilder<?, Y> yAxisBuilder) {
		if(!this.chartParameter) this.chartParameter = chartParameter
		if(!this.datasetProvider) this.datasetProvider = datasetProvider
		if(!XColumnType) XColumnType = xAxisBuilder.getDefaultQueryResultType()
		if(!YColumnType) YColumnType = yAxisBuilder.getDefaultQueryResultType()
		if(!XDataConverter) XDataConverter = xAxisBuilder.getDefaultDataConverter()
		if(!YDataConverter) YDataConverter = yAxisBuilder.getDefaultDataConverter()
	}

}
