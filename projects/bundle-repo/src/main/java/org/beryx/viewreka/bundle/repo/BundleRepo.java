package org.beryx.viewreka.bundle.repo;

import java.util.List;

/**
 * The interface implemented by a bundle repository
 */
public interface BundleRepo {
    /**
     * @return a list of {@link BundleInfo}, each entry describing a Viewreka bundle.
     * @throws Exception
     */
    List<BundleInfo> getEntries() throws Exception;

    /**
     * Force the reloading of the repository entries.
     * Implementations are allowed to delay the reloading until the next call of {@link #getEntries()}.
     * @throws Exception
     */
    void refresh() throws Exception;
}
