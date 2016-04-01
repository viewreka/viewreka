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
