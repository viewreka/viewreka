package org.beryx.vbundle.chart.xy

import org.beryx.viewreka.dsl.chart.FxChartBuilderBuilder
import org.beryx.viewreka.fxui.chart.xy.XYChartBuilder
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.Parameter

/**
 * A builder for {@link XYChartBuilder}s.
 */
class XYChartBuilderBuilder implements FxChartBuilderBuilder {
	@Override
	public <X,Y> XYChartBuilder<X,Y> build(String chartName, Closure closure, Parameter<?> chartParameter, Map<String, DatasetProvider> currentDataSetProviders) {
		XYChartBuilder<X,Y> xyChartBuilder = new XYChartBuilder<>()
		def chartDelegate = new XYChartDelegate(chartName, currentDataSetProviders, xyChartBuilder)
		closure.delegate = chartDelegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()

		xyChartBuilder.seriesConfigMap.each {seriesName, SeriesConfigImpl<X, Y> cfg ->
			cfg.configureDefaultsFrom(chartParameter, chartDelegate.dataset, xyChartBuilder.seriesDataBuilder.xAxisBuilder, xyChartBuilder.seriesDataBuilder.yAxisBuilder)
		}

		return xyChartBuilder;
	}
}
