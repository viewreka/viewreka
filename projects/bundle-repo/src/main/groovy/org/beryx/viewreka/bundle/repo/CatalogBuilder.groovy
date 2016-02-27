package org.beryx.viewreka.bundle.repo

import groovy.json.JsonOutput

/**
 * Helper class for building a bundle catalog and exporting it as JSON.
 */
class CatalogBuilder {
    final List<BundleInfo> entries = []

    def addBundle(URL bundleUrl) {
        def (_, entry) = new BundleReader().loadBundle(bundleUrl)
        entries << entry
    }

    String asJson() {
        JsonOutput.prettyPrint(JsonOutput.toJson(this))
    }
}
