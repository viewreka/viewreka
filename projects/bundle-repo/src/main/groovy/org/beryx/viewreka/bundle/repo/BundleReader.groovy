package org.beryx.viewreka.bundle.repo

import org.beryx.viewreka.bundle.api.ViewrekaBundle

/**
 * Helper class for reading a Viewreka bundle from a given URL.
 */
public class BundleReader {
    public static final String ATTR_BUNDLE_CLASS = 'viewrekaBundleClass'
    public static final String ATTR_VIEWREKA_VERSION_MAJOR = 'viewrekaVersionMajor'
    public static final String ATTR_VIEWREKA_VERSION_MINOR = 'viewrekaVersionMinor'
    public static final String ATTR_VIEWREKA_VERSION_PATCH = 'viewrekaVersionPatch'

    private final GroovyClassLoader classLoader

    static class Info implements BundleInfo {
        final int viewrekaVersionMajor
        final int viewrekaVersionMinor
        final int viewrekaVersionPatch
        @Delegate final ViewrekaBundle bundle
        final String url;

        public Info(int viewrekaVersionMajor, int viewrekaVersionMinor, int viewrekaVersionPatch, ViewrekaBundle bundle, String url) {
            this.viewrekaVersionMajor = viewrekaVersionMajor
            this.viewrekaVersionMinor = viewrekaVersionMinor
            this.viewrekaVersionPatch = viewrekaVersionPatch
            this.bundle = bundle
            this.url = url
        }

        @Override
        public String getBundleClass() {
            return bundle.getClass().name
        }

        @Override
        public String getHomePage() {
            return ""
        }

    }

    public BundleReader() {
        this(new GroovyClassLoader())
    }

    public BundleReader(GroovyClassLoader classLoader) {
        this.classLoader = classLoader
    }

    /**
     * @param bundleUrl the URL of the bundle to be loaded
     * @return the loaded data as a tuple: (ViewrekaBundle, BundleInfo)
     */
    def loadBundle(URL bundleUrl) {
        classLoader.addURL(bundleUrl)

        URL jarUrl = (bundleUrl.protocol == 'jar') ? bundleUrl : new URL("jar:${bundleUrl}!/")
        def jarFile = jarUrl.openConnection().jarFile
        String bundleClassName = jarFile.manifest.mainAttributes.getValue(ATTR_BUNDLE_CLASS)
        // No bundleClassName? Probably a regular jar.
        if(!bundleClassName) return [null, null]

        int versionMajor = jarFile.manifest.mainAttributes.getValue(ATTR_VIEWREKA_VERSION_MAJOR) as int
        int versionMinor = jarFile.manifest.mainAttributes.getValue(ATTR_VIEWREKA_VERSION_MINOR) as int
        int versionPatch = jarFile.manifest.mainAttributes.getValue(ATTR_VIEWREKA_VERSION_PATCH) as int

        def bundleClass = Class.forName(bundleClassName, true, classLoader)
        ViewrekaBundle bundle = bundleClass.getConstructor().newInstance()

        Info info = new Info(versionMajor, versionMinor, versionPatch, bundle, bundleUrl.toString())
        return [bundle, info]
    }
}
