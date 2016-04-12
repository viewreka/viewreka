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

import groovy.util.logging.Slf4j

/**
 * A {@link BundleRepo} implementation that loads the repository entries from a catalog located at a given URL.
 * <br>If a {@link CatalogCache} is available, this implementation first tries to retrieve the catalog from the cache.
 * <br>The catalog must be in a JSON format compatible with the one produced by a {@link CatalogBuilder}.
 */
@Slf4j
class CatalogRepo implements BundleRepo {
    final String catalogUrl
    final CatalogCache cache

    private boolean shouldRefresh

    CatalogRepo(String catalogUrl, CatalogCache cache = null) {
        this.catalogUrl = catalogUrl
        this.cache = cache
    }

    @Override
    List<BundleInfo> getEntries() throws Exception {
        List<BundleInfo> entries = shouldRefresh ? null : cache?.getEntries(catalogUrl)
        shouldRefresh = false
        if(entries) {
            log.debug("${entries.size()} entries of $catalogUrl read from cache")
        } else {
            entries = SimpleBundleInfo.fromJsonCatalogUrl(catalogUrl)
            log.debug("${entries?.size()} entries of $catalogUrl read from URL")
            cache?.putEntries(catalogUrl, entries)
        }
        entries
    }

    @Override
    void refresh() {
        shouldRefresh = true
    }
}
