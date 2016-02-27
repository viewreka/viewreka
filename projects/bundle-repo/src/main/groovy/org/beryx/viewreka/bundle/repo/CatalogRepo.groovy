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
