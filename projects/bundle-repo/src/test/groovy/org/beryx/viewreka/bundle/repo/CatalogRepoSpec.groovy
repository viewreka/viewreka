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
