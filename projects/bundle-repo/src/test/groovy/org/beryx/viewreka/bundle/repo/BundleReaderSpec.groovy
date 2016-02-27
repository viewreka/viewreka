package org.beryx.viewreka.bundle.repo

import groovy.json.JsonSlurper
import org.beryx.viewreka.bundle.api.ViewrekaBundle
import spock.lang.Specification

class BundleReaderSpec extends Specification {
    def reader = new BundleReader()

    def "should correctly load the bundle from a file"() {
        given:
        def bundlesDir = new File(getClass().getResource('/dummy-bundles').toURI())
        def fileName = bundlesDir.list{dir, name -> name.endsWith('.vbundle')}[0]
        def url = new File(bundlesDir, fileName).toURI().toURL()
        def (bundle, entry) = reader.loadBundle(url)
        println "Loaded bundle: $bundle.name"

        expect:
        bundle != null
        entry != null
    }

    def "should correctly load the bundle from an HTTPS URL"() {
        given:
        def url = new URL('https://bintray.com/artifact/download/viewreka/bundles/org.beryx.vbundle.ojdbc-7-1.0.0.vbundle')
        def (bundle, entry) = reader.loadBundle(url)
        println "Loaded bundle: $bundle.name"

        expect:
        bundle != null
        entry != null
    }
}
