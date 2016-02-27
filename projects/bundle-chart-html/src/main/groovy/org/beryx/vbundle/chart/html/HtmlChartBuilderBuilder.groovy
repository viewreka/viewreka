package org.beryx.vbundle.chart.html
import org.beryx.viewreka.dsl.chart.FxChartBuilderBuilder
import org.beryx.viewreka.fxui.chart.FxChartBuilder
import org.beryx.viewreka.fxui.chart.html.HtmlChartBuilder
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.Parameter
/**
 * A builder for {@link HtmlChartBuilder}s.
 */
class HtmlChartBuilderBuilder implements FxChartBuilderBuilder {
    @Override
    public <D> FxChartBuilder<D> build(String chartName, Closure closure, Parameter<?> chartParameter, Map<String, DatasetProvider> currentDataSetProviders) {

        def chartDelegate = new HtmlChartDelegate(chartName, currentDataSetProviders)
        closure.delegate = chartDelegate
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()

        return new HtmlChartBuilder(chartDelegate.webEngineConsumer)
    }
}
