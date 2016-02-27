package org.beryx.viewreka.dsl

import groovy.util.logging.Slf4j
import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.bundle.util.ParameterizedTemplate
import org.beryx.viewreka.core.Version

@Slf4j
class ViewBundle extends AbstractFxBundle {

    @Override String getId() { "org.beryx.vbundle.view" }
    @Override String getName() { "view" }
    @Override String getDescription() { "Viewreka view" }
    @Override List<String> getCategories() { ['other'] }
    @Override Version getVersion() { Version.current }

    @Override
    List<CodeTemplate> getTemplates() {
        return [
                new ParameterizedTemplate.Builder("view", 'view', """
                view <viewName> {
                    <#>
                }""".stripIndent()
            )
            .withDescription("Declare $name")
            .withIdParameter("viewName", "The name of this view", "My view")
            .build()
        ]
    }

    @Override
    String toString() {
        "$name $version"
    }
}
