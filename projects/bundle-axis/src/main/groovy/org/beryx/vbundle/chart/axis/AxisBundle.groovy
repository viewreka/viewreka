package org.beryx.vbundle.chart.axis

import groovy.util.logging.Slf4j
import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.bundle.util.ParameterizedTemplate
import org.beryx.viewreka.core.Version

@Slf4j
class AxisBundle extends AbstractFxBundle {
    @Override String getId() { "org.beryx.vbundle.chart.axis" }
    @Override String getName() { "standard axis" }
    @Override String getDescription() { "The Viewreka standard axis: date, double, float, int, long, sqlDate, sqlTime. sqlTimestamp, string" }
    @Override List<String> getCategories() { ['chart'] }
    @Override Version getVersion() { Version.current }

    @Override
    List<CodeTemplate> getTemplates() {
        ["x", "y"].collect {prefix ->
            new ParameterizedTemplate.Builder("${prefix}Axis", "view/chart/${prefix}Axis", """
                ${prefix}Axis(
                    type: <type>,
                    label: '<label>'
                )
                """.stripIndent()
            )
                    .withDescription("Declare $name")
                    .withParameter("type", "Type of values on the axis", "double", false)
                    .withParameter("label", "Axis label", "The axis label", false)
                    .build()
        }
    }

    @Override
    String toString() {
        "$name $version"
    }
}
