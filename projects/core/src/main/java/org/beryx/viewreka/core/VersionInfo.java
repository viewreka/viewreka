package org.beryx.viewreka.core;

public class VersionInfo {
    // Do not edit the values below. They are configured by gradle before the execution of the compile task, based on the values found in gradle.properties.
    private static final int MAJOR = 0;
    private static final int MINOR = 2;
    private static final int PATCH = 0;
    private static final String LABEL = "";
    private static final boolean RELEASE_BUILD = false;

    public static Version get() {
        return new Version(MAJOR, MINOR, PATCH, LABEL, RELEASE_BUILD);
    }
}
