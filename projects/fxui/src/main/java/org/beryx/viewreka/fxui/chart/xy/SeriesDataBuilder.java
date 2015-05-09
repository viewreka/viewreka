package org.beryx.viewreka.fxui.chart.xy;

import java.util.Collection;

import javafx.scene.chart.XYChart.Data;

public interface SeriesDataBuilder<X,Y> {
	AxisBuilder<?, X> getXAxisBuilder();
	AxisBuilder<?, Y> getYAxisBuilder();
	Collection<Data<X, Y>> createSeriesData(SeriesConfig<X,Y> seriesConfig);
}
