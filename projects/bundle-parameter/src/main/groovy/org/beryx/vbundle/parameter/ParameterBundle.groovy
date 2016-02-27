package org.beryx.vbundle.parameter

import groovy.util.logging.Slf4j
import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.bundle.util.ParameterizedTemplate
import org.beryx.viewreka.core.Version

@Slf4j
class ParameterBundle extends AbstractFxBundle {
    @Override String getId() { "org.beryx.vbundle.parameter" }
    @Override String getName() { "Standard parameters" }
    @Override String getDescription() { "The Viewreka standard parameters: date, double, float, int, long, sqlDate, sqlTime. sqlTimestamp, string" }
    @Override List<String> getCategories() { ['parameter'] }
    @Override Version getVersion() { Version.current }

    @Override
    List<CodeTemplate> getTemplates() {
        return [
                new ParameterizedTemplate.Builder("parameter (standard)", 'view/parameter', """
                parameter <prmName>(type: <prmType>) {
                    possibleValues(dataset: <dataset>)
                }""".stripIndent()
                )
                        .withDescription("Declare $name")
                        .withParameter("prmName", "parameter name", "prmSomething", false)
                        .withParameter("prmType", "parameter type", "string", false)
                        .withParameter("dataset", "dataset for possible values", "dsValues", true)
                        .build()
        ]
    }

    @Override
    String toString() {
        "$name $version"
    }
}
