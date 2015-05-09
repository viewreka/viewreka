package org.beryx.viewreka.dsl.chart.html

import static org.beryx.viewreka.core.Util.requireNonNull
import groovy.lang.Closure;

import java.util.Map;

import org.beryx.viewreka.dsl.chart.FxChartBuilderBuilder;
import org.beryx.viewreka.fxui.chart.FxChartBuilder;
import org.beryx.viewreka.fxui.chart.html.HtmlChartBuilder;
import org.beryx.viewreka.fxui.chart.xy.XYChartBuilder
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.Parameter

class HtmlChartBuilderBuilder implements FxChartBuilderBuilder {
	@Override
	public HtmlChartBuilder build(String chartName, Closure closure, Parameter<?> chartParameter, Map<String, DatasetProvider> currentDataSetProviders) {

		def chartDelegate = new HtmlChartDelegate(chartName, currentDataSetProviders)
		closure.delegate = chartDelegate
		closure.resolveStrategy = Closure.DELEGATE_FIRST
		closure.call()

		return new HtmlChartBuilder(chartDelegate.webEngineConsumer)
	}
}
