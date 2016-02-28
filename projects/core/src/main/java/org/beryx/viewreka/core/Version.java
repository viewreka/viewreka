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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

public class Version implements Comparable<Version> {
    private final int major;
    private final int minor;
    private final int patch;
    private final String label;
    private final boolean releaseBuild;

    public Version(int major, int minor, int patch) {
        this(major, minor, patch, "", true);
    }

    public Version(int major, int minor, int patch, String label, boolean releaseBuild) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.label = StringUtils.isBlank(label) ? "" : label;
        this.releaseBuild = releaseBuild;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getLabel() {
        return label;
    }

    public boolean isReleaseBuild() {
        return releaseBuild;
    }

    @Override
    public String toString() {
        // This implementation is similar to https://github.com/ethankhall/gradle-semantic-versioning/blob/master/src/main/groovy/io/ehdev/version/Version.groovy
        String versionString = major + "." + minor + "." + patch;
        if(!StringUtils.isBlank(label)) {
            versionString += "-" + label;
        }
        if(!releaseBuild) {
            versionString += "-SNAPSHOT";
        }
        return versionString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        if (major != version.major) return false;
        if (minor != version.minor) return false;
        if (patch != version.patch) return false;
        if (releaseBuild != version.releaseBuild) return false;
        return label != null ? label.equals(version.label) : version.label == null;
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + patch;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (releaseBuild ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(Version v) {
        return new CompareToBuilder()
                .append(major, v.major)
                .append(minor, v.minor)
                .append(patch, v.patch)
                .append(label, v.label)
                .append(releaseBuild, v.releaseBuild)
                .build();
    }
}
