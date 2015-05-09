package org.beryx.viewreka.dsl;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;

import org.beryx.viewreka.core.ViewrekaException;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.util.logging.Slf4j;

@Slf4j
public class ViewrekaClassLoader extends GroovyClassLoader {
	def ViewrekaClassLoader(GroovyCodeSource scriptSource) {
		if(scriptSource.url?.protocol == 'file') {
			def scriptFile = new File(scriptSource.url.file)
			if(scriptFile.parent) {
				getClassLoaderUrls(scriptFile.parentFile).each {url -> log.debug("Adding to classpath: $url"); addURL(url)}
			}
		}
	}

	def URL[] getClassLoaderUrls(File projectDir) {
		URL[] jarUrls = [];
		File libDir = new File(projectDir, "lib");
		String[] jarNames = libDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".jar");
			}
		});
		if((jarNames != null) && (jarNames.length > 0)) {
			jarUrls = new URL[jarNames.length];
			for(int i=0; i<jarNames.length; i++) {
				File jarFile = new File(libDir, jarNames[i]);
				try {
					jarUrls[i] =jarFile.toURI().normalize().toURL();
				} catch (MalformedURLException e) {
					throw new ViewrekaException("Cannot obtain the URL of " + jarFile.getAbsolutePath(), e);
				}
			}
		}
		return jarUrls;
	}
}
