package org.beryx.viewreka.dsl.chart.xy;

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
