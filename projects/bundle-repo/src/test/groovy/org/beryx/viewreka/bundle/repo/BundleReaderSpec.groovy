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
