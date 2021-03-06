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
package org.beryx.vbundle.chart.html

import javafx.scene.layout.Pane
import org.beryx.viewreka.dsl.chart.ChartHandler
import org.beryx.viewreka.dsl.chart.FxChartBuilderBuilder

/**
 * The {@link ChartHandler} associated with the <code>html</code> and <code>htmlChart</code> aliases.
 */
class HtmlChartHandler implements ChartHandler<String,Pane> {

    @Override
    public Class<? extends FxChartBuilderBuilder> getAliasClass() {
        return HtmlChartBuilderBuilder
    }

    @Override
    public Collection<String> getAliases() {
        return Arrays.asList("html", "htmlChart")
    }

}
