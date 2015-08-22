package org.beryx.viewreka.dsl.chart.html

import org.beryx.viewreka.dsl.chart.ChartHandler
import org.beryx.viewreka.dsl.chart.FxChartBuilderBuilder

/**
 * The {@link ChartHandler} associated with the <code>html</code> and <code>htmlChart</code> aliases.
 */
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
