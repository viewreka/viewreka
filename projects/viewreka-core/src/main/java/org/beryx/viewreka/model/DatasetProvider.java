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
package org.beryx.viewreka.model;

import java.util.function.Consumer;


/**
 * A dataset provider.
 */
public interface DatasetProvider extends Parameterized, AutoCloseable {
    /**
     * @return the name of this provider
     */
    String getName();

    /**
     * Retrieves the dataset. A typical implementation will try to return the same dataset as the previous invocation,
     * but it will create a new dataset if this provider has been marked as dirty or if the previous dataset has been closed.
     * @return the dataset.
     */
    Dataset getDataset();

    /**
     * Marks this provider as dirty. The provider remains dirty until the next successful invocation of {@link #getDataset()}.
     */
    void setDirty();

    /**
     * Adds a listener that gets notified when this provider becomes dirty.
     * @param listener the listener that gets notified when this provider becomes dirty.
     * @return true, if the listener has been successfully added.
     */
    boolean addDirtyListener(Consumer<DatasetProvider> listener);

    /**
     * Removes a dirty listener.
     * @param listener the listener to be removed.
     * @return true, if the listener was found and removed.
     */
    boolean removeDirtyListener(Consumer<DatasetProvider> listener);
}
