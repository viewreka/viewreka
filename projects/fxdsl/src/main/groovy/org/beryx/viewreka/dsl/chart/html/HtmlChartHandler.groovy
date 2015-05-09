package org.beryx.viewreka.dsl.chart.html

import java.util.Collection

import org.beryx.viewreka.dsl.chart.ChartHandler;
import org.beryx.viewreka.dsl.chart.FxChartBuilderBuilder;

class HtmlChartHandler implements ChartHandler {

	@Override
	public Class<? extends FxChartBuilderBuilder> getAliasClass() {
		return HtmlChartBuilderBuilder
	}

	@Override
	public Collection<String> getAliases() {
		return Arrays.asList("html", "htmlChart")
	}

}
