/**
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
package org.beryx.viewreka.bundle.repo;

import java.util.List;

/**
 * Cache for bundle catalogs
 */
public interface CatalogCache {
    @FunctionalInterface
    public static interface ErrorReporter {
        void report(String message, Throwable t);
    }

    /**
     * Sets the {@link ErrorReporter} used by {@link #getEntries(String)} and {@link #putEntries(String, List)}
     * @param reporter the error reporter
     */
    void setErrorReporter(ErrorReporter reporter);

    /**
     * Retrieves a bundle catalog from the cache.
     * <br>This method should not throw exceptions. Instead, errors should be reported using the configured {@link ErrorReporter}.
     * @param catalogUrl the URL of the cached catalog to be retrieved
     * @return a list of {@link BundleInfo}, each item describing a Viewreka bundle;
     * or null, if a catalog for the given URL cannot be retrieved (because it does not exist in the cache or due to an error)
     */
    List<BundleInfo> getEntries(String catalogUrl);

    /**
     * Stores a bundle catalog into the cache.
     * <br>This method should not throw exceptions. Instead, errors should be reported using the configured {@link ErrorReporter}.
     * @param catalogUrl the URL of the catalog to be stored
     * @param entries the list of {@link BundleInfo} entries that constitute the bundle catalog
     * @return true, if the catalog has been successfully stored
     */
    boolean putEntries(String catalogUrl, List<BundleInfo> entries);
}
