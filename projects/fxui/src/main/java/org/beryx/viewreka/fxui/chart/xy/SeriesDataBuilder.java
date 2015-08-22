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
