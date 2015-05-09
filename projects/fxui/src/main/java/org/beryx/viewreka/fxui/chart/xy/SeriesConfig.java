package org.beryx.viewreka.fxui.chart.xy;

import java.util.function.Function;

import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.Parameter;

public interface SeriesConfig<X,Y> {
	DatasetProvider getDatasetProvider();
	int getXColumn();
	String getXColumnName();
	int getYColumn();
	String getYColumnName();
	int getExtraColumn();
	String getExtraColumnName();
	Parameter<?> getChartParameter();
	Class<?> getXColumnType();
	Class<?> getYColumnType();
	Function<?, X> getXDataConverter();
	Function<?, Y> getYDataConverter();
}
