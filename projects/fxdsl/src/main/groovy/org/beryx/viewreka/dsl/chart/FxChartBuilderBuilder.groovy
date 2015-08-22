package org.beryx.viewreka.dsl.chart

import org.beryx.viewreka.fxui.chart.FxChartBuilder
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.Parameter

/**
 * Interface for building {@link FxChartBuilder}s.
 */
interface FxChartBuilderBuilder {
    def <D> FxChartBuilder<D> build(String chartName, Closure closure, Parameter<?> chartParameter, Map<String, DatasetProvider> currentDataSetProviders);
}
