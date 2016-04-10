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

class H2CatalogCacheSpec extends Specification {

    List<BundleInfo> catalog1 = makeCatalog(6)
    List<BundleInfo> catalog2 = makeCatalog(4)

    static makeCatalog(int entryCount) {
        def catalogBuilder = new CatalogBuilder()
        def bundlesDir = new File(getClass().getResource('/dummy-bundles').toURI())
        bundlesDir.list{dir, name -> name.endsWith('.vbundle')}
                .collect { String name -> new File(bundlesDir, name).toURI().toURL()}
                .each {url -> catalogBuilder.addBundle(url)}
        catalogBuilder.entries.subList(0, entryCount)
    }

    static String deleteAndGetCachePath(String cacheName) {
        File tmpDir = new File(System.properties['java.io.tmpdir'], 'h2cache-test')
        assert tmpDir.deleteDir()
        String basePath = tmpDir.path.replaceAll('\\\\', '/')
        "${basePath}/${cacheName}"
    }

    def "should correctly store and retrieve catalogs"() {
        given:
        def cachePath = deleteAndGetCachePath('cacheH2')
        println "cachePath: $cachePath"
        def cache = new H2CatalogCache(cachePath)
        def urlDefault = 'http://catalogs.example.com/default'
        def urlCustom = 'http://catalogs.example.com/custom'

        expect:
        cache.getEntries(urlDefault) == null
        cache.getEntries(urlCustom) == null

        when:
        cache.putEntries(urlDefault, catalog1)

        then:
        cache.getEntries(urlDefault).size() == catalog1.size()
        cache.getEntries(urlCustom) == null

        when:
        cache.putEntries(urlDefault, catalog2)

        then:
        cache.getEntries(urlDefault).size() == catalog2.size()
        cache.getEntries(urlCustom) == null
    }
}
