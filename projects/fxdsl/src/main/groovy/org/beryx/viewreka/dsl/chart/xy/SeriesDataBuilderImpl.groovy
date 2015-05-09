package org.beryx.viewreka.dsl.chart.xy

import groovy.util.logging.Slf4j
import javafx.scene.chart.XYChart.Data

import org.beryx.viewreka.fxui.chart.xy.AxisBuilder
import org.beryx.viewreka.fxui.chart.xy.SeriesConfig;
import org.beryx.viewreka.fxui.chart.xy.SeriesDataBuilder
import org.beryx.viewreka.model.Dataset
import org.beryx.viewreka.parameter.Parameter

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
		int itemCount = dataset.getItemCount();
		for(int i=0; i<itemCount; i++) {
			Object xQueryVal = (cfg.XColumn < 0)
					? dataset.getValue(cfg.XColumnName, i, xQueryResultClass)
					: dataset.getValue(cfg.XColumn, i, xQueryResultClass)
			if(xQueryVal == null) {
				log.error("xQueryVal is null for column ${cfg.XColumn}, item $i of dataset: $dataset");
				continue;
			}
			X xVal = cfg.XDataConverter ? cfg.XDataConverter.apply(xQueryVal) : xAxisBuilder?.defaultDataConverter.apply(xQueryVal)

			Object yQueryVal = (cfg.YColumn < 0)
					? dataset.getValue(cfg.YColumnName, i, yQueryResultClass)
					: dataset.getValue(cfg.YColumn, i, yQueryResultClass)
			if(yQueryVal == null) {
				log.error("yQueryVal is null for column ${cfg.YColumn}, item $i of dataset: $dataset");
				continue;
			}
			Y yVal = cfg.YDataConverter ? cfg.YDataConverter.apply(yQueryVal) : yAxisBuilder?.defaultDataConverter.apply(yQueryVal)


			// TODO - extraConverter
			Object extraVal = (cfg.extraColumn >= 0)
				? dataset.getObject(cfg.extraColumn, i)
				: (cfg.extraColumnName == null)
				? null
				: dataset.getObject(cfg.extraColumnName, i);
			if(extraVal instanceof Number) {
				extraVal = 100.0 * ((Number)extraVal).doubleValue();
			}

			Data<X,Y> data = new Data<>(xVal, yVal, extraVal);
			seriesData.add(data)
		}
	}

}
