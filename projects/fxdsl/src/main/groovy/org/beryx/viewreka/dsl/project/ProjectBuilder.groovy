package org.beryx.viewreka.dsl.project;

import groovy.lang.GroovyCodeSource;
import groovy.lang.Script;
import groovy.util.logging.Slf4j

import org.apache.commons.io.FilenameUtils;
import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.dsl.ViewrekaClassLoader;
import org.beryx.viewreka.dsl.ViewrekaScript;
import org.beryx.viewreka.dsl.data.DataSourceHandler;
import org.beryx.viewreka.dsl.transform.Keyword;
import org.beryx.viewreka.dsl.transform.ViewrekaDSL;
import org.beryx.viewreka.fxui.FxProject;
import org.beryx.viewreka.model.ProjectModel;
import org.beryx.viewreka.settings.ProjectSettings;
import org.beryx.viewreka.settings.ProjectSettingsImpl;
import org.beryx.viewreka.settings.ProjectSettingsManager;
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
import org.codehaus.groovy.control.customizers.ImportCustomizer;

@Slf4j
class ProjectBuilder {

	static FxProject createFromResource(String resourcePath) {
		def url = this.class.getResource(resourcePath)
		if(!url) throw new ViewrekaException("Resource $resourcePath not found")
		log.info("url: $url")
		def scriptSource = new GroovyCodeSource(url.toURI().normalize())
		return createFromGroovyCodeSource(scriptSource)
	}

	static FxProject createFromUri(String uri) {
		def scriptSource = new GroovyCodeSource(new URI(uri))
		return createFromGroovyCodeSource(scriptSource)
	}

	static FxProject createFromGroovyCodeSource(GroovyCodeSource scriptSource) {
		Exception scriptException = null
		FxProjectImpl project = new FxProjectImpl()
		project.scriptText = scriptSource.scriptText
		try {
			if(scriptSource.url?.protocol == 'file') {
				def scriptFile = new File(scriptSource.url.file)
				project.name = FilenameUtils.getBaseName(scriptFile.name)
				project.projectSettingsManager = new ProjectSettingsManager(scriptFile.parent ?: '.', "${project.name}.settings.xml")
				project.scriptFile = scriptFile
			}

			def conf = new CompilerConfiguration()
			conf.setDebug(true)
			conf.setOutput(new PrintWriter(System.out))

			conf.setScriptBaseClass(ViewrekaScript.class.name)
			conf.getScriptExtensions().add('viewreka')

			def ASTTransformationCustomizer customizer = new ASTTransformationCustomizer(
					keywords: [Keyword.Datasource, Keyword.View, Keyword.Dataset, Keyword.Parameter, Keyword.Chart],
					ViewrekaDSL)
			conf.addCompilationCustomizers(customizer)

			def gcl = new ViewrekaClassLoader(scriptSource)
			Thread.currentThread().setContextClassLoader(gcl)
			def shell = new GroovyShell(gcl, conf)

			ViewrekaScript script = shell.parse(scriptSource);
			shell.context.setProperty("project", project)
			script.setBinding(shell.context);
			script.run();
		} catch (Exception e) {
			project.scriptIssues << ScriptIssueImpl.fromThrowable(e)
		}

		return project
	}
}
