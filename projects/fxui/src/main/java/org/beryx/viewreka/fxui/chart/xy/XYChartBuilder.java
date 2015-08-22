package org.beryx.viewreka.fxui.chart.xy;

import static org.beryx.viewreka.core.Util.check;
import static org.beryx.viewreka.core.Util.checkNotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

import org.beryx.viewreka.chart.ChartController;
import org.beryx.viewreka.fxui.chart.AbstractFxChartBuilder;

/**
 * A chart builder for {@link XYChart}s.
 * @param <X> the type of the X axis
 * @param <Y> the type of the Y axis
 */
public class XYChartBuilder<X,Y> extends AbstractFxChartBuilder<XYChartData<X,Y>> {
	private SeriesDataBuilder<X, Y> seriesDataBuilder;
	private final Map<String, XYChartCreator> chartCreators = new LinkedHashMap<>();

	private final Map<String, SeriesConfig<X,Y>> seriesConfigMap = new LinkedHashMap<>();

	@Override
	public ChartController<XYChartData<X, Y>> createController(Pane chartParentPane) {
		checkNotNull(seriesDataBuilder, "seriesDataBuilder");
		checkNotNull(seriesDataBuilder.getXAxisBuilder(), "xAxisBuilder");
		checkNotNull(seriesDataBuilder.getYAxisBuilder(), "yAxisBuilder");
		check(!chartCreators.isEmpty(), "chartCreators is empty");

		XYChartCreator chartCreator = null;
		String currentStyle = getCurrentChartStyle();
		if(currentStyle != null) {
			chartCreator = chartCreators.get(currentStyle);
		}
		if(chartCreator == null) {
			Entry<String, XYChartCreator> entry = chartCreators.entrySet().iterator().next();
			currentStyle = entry.getKey();
			chartCreator = entry.getValue();
		}
		return new XYChartController<>(chartParentPane, seriesConfigMap, seriesDataBuilder, chartCreator, getTitleSupplier(), getStylesheetSupplier());
	}

	/**
	 * @return the builder used to create series data and the X and Y axes
	 */
	public SeriesDataBuilder<X, Y> getSeriesDataBuilder() {
		return seriesDataBuilder;
	}
	/**
	 * @param seriesDataBuilder the builder used to create series data and the X and Y axes
	 */
	public void setSeriesDataBuilder(SeriesDataBuilder<X, Y> seriesDataBuilder) {
		this.seriesDataBuilder = seriesDataBuilder;
	}

	@Override
	public List<String> getChartStyles() {
		return new ArrayList<>(chartCreators.keySet());
	}

	/**
	 * Retrieves the chart creators
	 * @return a map of {@link XYChartCreator}s indexed by their names
	 */
	public Map<String, XYChartCreator> getChartCreators() {
		return chartCreators;
	}

	/**
	 * Retrieves the map of chart series configurations.
	 * @return a map of {@link SeriesConfig}s indexed by their names
	 */
	public Map<String, SeriesConfig<X, Y>> getSeriesConfigMap() {
		return seriesConfigMap;
	}
}
