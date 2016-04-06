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
import groovy.util.logging.Slf4j
import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.api.ViewrekaBundle
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.bundle.util.ParameterizedTemplate
import org.beryx.viewreka.bundle.util.SimpleParameter
import org.beryx.viewreka.core.Version

@Slf4j
class HtmlChartBundle extends AbstractFxBundle {
    @Override String getId() { "org.beryx.vbundle.chart.html" }
    @Override String getName() { "HTML chart" }
    @Override String getDescription() { "Viewreka HTML chart" }
    @Override List<String> getCategories() { ['chart'] }
    @Override Version getVersion() { Version.current }

    @Override
    List<CodeTemplate> getTemplates() {
        return [
                new ParameterizedTemplate.Builder("chart (HTML)", 'view/chart', """
                chart <chartName>(type: html) {
                    content = <htmlContent>
                }""".stripIndent()
                )
                        .withDescription("Declare $name")
                        .withParameter("chartName", "chart name", "chartSomething", false)
                        .withParameter(new SimpleParameter.Builder("htmlContent", "<h1>Hello</h1>")
                            .withControl(HtmlChartContentPane, {pane -> pane.content}, {pane,content -> pane.setInlineContent(content)})
                            .build())
                        .build()
        ]
    }

    @Override
    String toString() {
        "$name $version"
    }
}
