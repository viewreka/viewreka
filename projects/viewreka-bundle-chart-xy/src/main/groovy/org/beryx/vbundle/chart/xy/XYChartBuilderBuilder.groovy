/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.vbundle.chart.xy

import org.beryx.viewreka.dsl.chart.FxChartBuilderBuilder
import org.beryx.viewreka.fxui.chart.xy.XYChartBuilder
import org.beryx.viewreka.fxui.chart.xy.XYChartData
import org.beryx.viewreka.model.DatasetProvider
import org.beryx.viewreka.parameter.Parameter

/**
 * A builder for {@link XYChartBuilder}s.
 */
class XYChartBuilderBuilder<X,Y> implements FxChartBuilderBuilder<XYChartData<X,Y>> {
	@Override
	public XYChartBuilder<X,Y> build(String chartName, Closure closure, Parameter<?> chartParameter, Map<String, DatasetProvider> currentDataSetProviders) {
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
