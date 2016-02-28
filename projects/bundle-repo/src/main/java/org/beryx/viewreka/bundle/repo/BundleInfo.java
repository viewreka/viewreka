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

import org.beryx.viewreka.core.Version;

import java.util.List;

/**
 * Info about a Viewreka bundle
 */
public interface BundleInfo {
    /**
     * @return the fully qualified name of the bundle class (for example: {@code org.example.foobar.FooBarBundle}),
     * which must implement the {@link org.beryx.viewreka.bundle.api.ViewrekaBundle} interface.
     */
    String getBundleClass();

    /**
     * @return the major number of the oldest Viewreka version supported by this bundle.
     */
    int getViewrekaVersionMajor();

    /**
     * @return the minor number of the oldest Viewreka version supported by this bundle.
     */
    int getViewrekaVersionMinor();

    /**
     * @return the patch number of the oldest Viewreka version supported by this bundle.
     */
    int getViewrekaVersionPatch();

    /**
     * Provides a list of strings identifying the categories to which this bundle belongs.
     * <br/>Most bundles belong to only one category, therefore this method will usually return a single-element list.
     * <br/>Typical categories are: {@code chart}, {@code datasource}, {@code parameter}, and {@code other}.
     * It is recommended to stick with these standard categories unless you have a good reason to use a new one.
     * @return the list of categories to which this bundle belongs.
     */
    List<String> getCategories();

    /**
     * @return a string that identifies the bundle. It is recommended to use the reverse domain name style (for example: {@code org.example.foobar})
     * in order to avoid bundle ID collisions.
     */
    String getId();

    /**
     * @return a user friendly name for this bundle (for example: {@code FooBar Chart})
     */
    String getName();

    /**
     * @return the version of this bundle
     */
    Version getVersion();

    /**
     * @return a suggested file name for this bundle.
     *
     * <br/>This default implementation joins {@link #getId()} and {@link #getVersion()} using {@code "-"} as delimiter, and appends the extension {@code ".vbundle"}.
     *
     * <br/>{@code Example:} if {@link #getId()} returns {@code "org.example.foobar"}, and {@link #getVersion()} returns {@code "2.4.13"},
     * the suggested file name will be: {@code "org.example.foobar-2.4.13.vbundle"}.
     */
    default String getFileName() {
        return getId() + "-" + getVersion() + ".vbundle";
    }

    /**
     * @return a description of this bundle
     */
    String getDescription();

    /**
     * @return the URL from which the bundle can be downloaded
     */
    String getUrl();

    /**
     * @return the bundle's homepage
     */
    String getHomePage();
}
