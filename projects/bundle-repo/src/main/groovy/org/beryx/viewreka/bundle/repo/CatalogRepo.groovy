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
import org.beryx.viewreka.core.Version

/**
 * A {@link BundleRepo} implementation that loads the repository entries from a catalog located at a given URL.
 * The catalog must be in a JSON format compatible with the one produced by a {@link CatalogBuilder}.
 */
class CatalogRepo implements BundleRepo {
    final String catalogUrl
    private List<BundleInfo> cachedEntries = null

    CatalogRepo(String catalogUrl) {
        this.catalogUrl = catalogUrl
    }

    @Override
    List<BundleInfo> getEntries() throws Exception {
        if(cachedEntries == null) {
            cachedEntries = []
            Map catalog = new JsonSlurper().parse(new URL(catalogUrl))
            catalog.entries.each { Map entry ->
                def bundleInfo = new BundleInfo() {
                    @Override String getBundleClass() { entry.bundleClass }
                    @Override int getViewrekaVersionMajor() { entry.viewrekaVersionMajor as int }
                    @Override int getViewrekaVersionMinor() { entry.viewrekaVersionMinor as int }
                    @Override int getViewrekaVersionPatch() { entry.viewrekaVersionPatch as int }
                    @Override List<String> getCategories() { entry.categories }
                    @Override String getId() { entry.id }
                    @Override String getName() { entry.name }
                    @Override Version getVersion() { entry.version }
                    @Override String getDescription() { entry.description }
                    @Override String getUrl() { entry.url }
                    @Override String getHomePage() { entry.homePage }
                    @Override String toString() { "$entry.name $entry.version" }
                }
                cachedEntries << bundleInfo
            }
        }
        cachedEntries
    }

    @Override
    void refresh() {
        cachedEntries = null
    }
}
