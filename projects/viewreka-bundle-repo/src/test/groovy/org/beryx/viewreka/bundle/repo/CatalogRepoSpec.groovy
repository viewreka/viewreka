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
package org.beryx.viewreka.bundle.repo

import spock.lang.Specification

class CatalogRepoSpec extends Specification {
    def "should correctly write a catalog and read from it"() {
        given:
        def catalogBuilder = new CatalogBuilder()
        def bundlesDir = new File(getClass().getResource('/dummy-bundles').toURI())
        bundlesDir.list{dir, name -> name.endsWith('.vbundle')}
            .collect { String name -> new File(bundlesDir, name).toURI().toURL()}
                .each {url -> catalogBuilder.addBundle(url)}
        def catalogText = catalogBuilder.asJson()
        def catalogFile = File.createTempFile("catalog", ".json")
        catalogFile << catalogText

        when:
        def catalog = new CatalogRepo(catalogFile.toURI().toURL().toString())

        then:
        catalog.entries.size() == 8
        BundleInfo streamgraph = catalog.entries.find { entry -> entry.id == 'streamgraph'}
        streamgraph.bundleClass == 'org.beryx.dummy.StreamgraphBundle'
        streamgraph.categories.contains('chart')
        streamgraph.categories.contains('parameter')
    }
}
