package org.beryx.vbundle.chart.xy

import groovy.util.logging.Slf4j
import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.bundle.util.ParameterizedTemplate
import org.beryx.viewreka.core.Version

@Slf4j
class XYChartBundle extends AbstractFxBundle {
    @Override String getId() { "org.beryx.vbundle.chart.xy" }
    @Override String getName() { "XY chart" }
    @Override String getDescription() { "Viewreka XY chart" }
    @Override List<String> getCategories() { ['chart'] }
    @Override Version getVersion() { Version.current }

    @Override
    List<CodeTemplate> getTemplates() {
        return [
                new ParameterizedTemplate.Builder("chart (XY)", 'view/chart', """
                chart <chartName>(type: xy) {
                    dataset = <dataset>
                    xAxis(type: <xType>, label: '<xLabel>')
                    yAxis(type: <yType>, label: '<yLabel>')
                    series(<seriesName>: ['<xValCol>','<yValCol>'])
                }""".stripIndent()
                )
                        .withDescription("Declare $name")
                        .withParameter("chartName", "chart name", "chartSomething", false)
                        .withParameter("dataset", "dataset", "dsSomething", false)
                        .withParameter("xType", "Type of values on the X axis", "double", false)
                        .withParameter("xLabel", "X axis label", "The X Axis", false)
                        .withParameter("yType", "Type of values on the Y axis", "double", false)
                        .withParameter("yLabel", "Y axis label", "The Y Axis", false)
                        .withParameter("seriesName", "series name", "series1", false)
                        .withParameter("xValCol", "Column for values the X axis", "xCol", false)
                        .withParameter("yValCol", "Column for values the Y axis", "yCol", false)
                        .build()
        ]
    }

    @Override
    String toString() {
        "$name $version"
    }
}
