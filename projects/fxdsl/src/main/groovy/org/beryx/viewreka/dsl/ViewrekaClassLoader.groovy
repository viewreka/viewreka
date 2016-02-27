package org.beryx.viewreka.dsl
import groovy.util.logging.Slf4j
import org.beryx.viewreka.bundle.api.ViewrekaBundle
import org.beryx.viewreka.bundle.repo.BundleReader
import org.beryx.viewreka.core.ViewrekaException
/**
 * The class loader used by Viewreka to load all classes belonging to a project.
 */
@Slf4j
public class ViewrekaClassLoader extends GroovyClassLoader {
    final bundleReader = new BundleReader(this)


    List<ViewrekaBundle> configureScript(GroovyCodeSource scriptSource) {
        if(scriptSource.url?.protocol == 'file') {
            def scriptFile = new File(scriptSource.url.file)
            if(scriptFile.parent) {
                configureURLs(getClassLoaderUrls(scriptFile.parentFile))
            }
        }
    }

    List<ViewrekaBundle> configureURLs(URL... urls) {
        List<ViewrekaBundle> bundles = []
        urls.each {url ->
            def (bundle, _) = bundleReader.loadBundle(url)
            if(bundle) bundles << bundle
        }
        return bundles
    }


    static def URL[] getClassLoaderUrls(File projectDir) {
        URL[] jarUrls = [];
        File libDir = LibDirProvider.getLibDir(projectDir);
        String[] jarNames = libDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar") || name.endsWith(".vbundle");
            }
        });
        if((jarNames != null) && (jarNames.length > 0)) {
            jarUrls = new URL[jarNames.length];
            for(int i=0; i<jarNames.length; i++) {
                File jarFile = new File(libDir, jarNames[i]);
                try {
                    jarUrls[i] = jarFile.toURI().normalize().toURL();
                } catch (MalformedURLException e) {
                    throw new ViewrekaException("Cannot obtain the URL of " + jarFile.getAbsolutePath(), e);
                }
            }
        }
        return jarUrls;
    }
}
