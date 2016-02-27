package org.beryx.vbundle.sql

import groovy.util.logging.Slf4j
import org.beryx.viewreka.bundle.api.CodeTemplate
import org.beryx.viewreka.bundle.util.AbstractFxBundle
import org.beryx.viewreka.bundle.util.ParameterizedTemplate
import org.beryx.viewreka.core.Version

@Slf4j
class SqlBundle extends AbstractFxBundle {
    @Override String getId() { "org.beryx.vbundle.sql" }
    @Override String getName() { "SQL datasource" }
    @Override String getDescription() { "The Viewreka SQL datasource" }
    @Override List<String> getCategories() { ['datasource'] }
    @Override Version getVersion() { Version.current }

    @Override
    List<CodeTemplate> getTemplates() {
        return [
                new ParameterizedTemplate.Builder("dataset (SQL)", 'view/dataset', """
                dataset <dsName> {
                  query = <query>
                }""".stripIndent()
                )
                        .withDescription("Declare $name")
                        .withParameter("dsName", "dataset name", "dsSomething", false)
                        .withParameter("query", "SQL query", "'select * from my_table'", false)
                        .build()
        ]
    }

    @Override
    String toString() {
        "$name $version"
    }
}
