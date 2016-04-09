/*
 * Copyright 2016 the original author or authors.
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
import org.beryx.viewreka.core.Version

class SimpleBundleInfo implements BundleInfo {
    def final item

    static List<SimpleBundleInfo> fromJsonCatalog(String json) {
        fromCatalogMap(new JsonSlurper().parse(json))
    }

    static List<SimpleBundleInfo> fromJsonCatalogUrl(String url) {
        fromCatalogMap(new JsonSlurper().parse(new URL(url)))
    }

    static List<SimpleBundleInfo> fromJsonInputStream(InputStream inputStream, String charset='UTF-8') {
        fromCatalogMap(new JsonSlurper().parse(inputStream, charset))
    }

    static List<SimpleBundleInfo> fromCatalogMap(Map catalogMap) {
        List<SimpleBundleInfo> entries = []
        catalogMap.entries.each { Map entry ->
            entries << new SimpleBundleInfo(entry)
        }
        entries
    }


    private SimpleBundleInfo(item) {
        this.item = item
    }

    @Override String getBundleClass() { item.bundleClass }
    @Override int getViewrekaVersionMajor() { item.viewrekaVersionMajor as int }
    @Override int getViewrekaVersionMinor() { item.viewrekaVersionMinor as int }
    @Override int getViewrekaVersionPatch() { item.viewrekaVersionPatch as int }
    @Override List<String> getCategories() { item.categories }
    @Override String getId() { item.id }
    @Override String getName() { item.name }
    @Override Version getVersion() { new Version(item.version.major, item.version.minor, item.version.patch, item.version.label, item.version.releaseBuild) }
    @Override String getDescription() { item.description }
    @Override String getUrl() { item.url }
    @Override String getHomePage() { item.homePage }
    @Override String toString() { "$item.name $item.version" }

}
