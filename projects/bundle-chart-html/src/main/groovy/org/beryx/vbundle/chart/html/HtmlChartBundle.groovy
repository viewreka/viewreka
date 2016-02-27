package org.beryx.vbundle.chart.html
import groovy.util.logging.Slf4j
import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.api.ViewrekaBundle
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.bundle.util.ParameterizedTemplate
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
                        .withParameter("htmlContent", "HTML content", "'<h1>Hello</h1>'", false)
                        .build()
        ]
    }

    @Override
    String toString() {
        "$name $version"
    }
}
