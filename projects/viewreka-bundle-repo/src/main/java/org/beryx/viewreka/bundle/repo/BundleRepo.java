/**
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
package org.beryx.viewreka.bundle.repo;

import java.util.List;

/**
 * The interface implemented by a bundle repository
 */
public interface BundleRepo {
    /**
     * @return a list of {@link BundleInfo}, each item describing a Viewreka bundle.
     * @throws Exception
     */
    List<BundleInfo> getEntries() throws Exception;

    /**
     * Force the reloading of the repository entries.
     * Implementations are allowed to delay the reloading until the next call of {@link #getEntries()}.
     * @throws Exception
     */
    void requestRefresh() throws Exception;
}
