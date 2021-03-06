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
package org.beryx.viewreka.core;

public class VersionInfo {
    // Do not edit the values below. They are configured by gradle before the execution of the compile task, based on the values found in gradle.properties.
    private static final int MAJOR = 0;
    private static final int MINOR = 3;
    private static final int PATCH = 0;
    private static final String LABEL = "";
    private static final boolean RELEASE_BUILD = false;

    public static Version get() {
        return new Version(MAJOR, MINOR, PATCH, LABEL, RELEASE_BUILD);
    }
}
