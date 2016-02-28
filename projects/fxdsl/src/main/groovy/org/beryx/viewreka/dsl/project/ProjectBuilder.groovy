/*
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
package org.beryx.viewreka.dsl.project

import groovy.util.logging.Slf4j
import org.apache.commons.io.FilenameUtils
import org.beryx.viewreka.bundle.api.ViewrekaBundle
import org.beryx.viewreka.core.ViewrekaException
import org.beryx.viewreka.dsl.ViewrekaClassLoader
import org.beryx.viewreka.dsl.ViewrekaScript
import org.beryx.viewreka.dsl.transform.Keyword
import org.beryx.viewreka.dsl.transform.ViewrekaDSL
import org.beryx.viewreka.fxui.FxProject
import org.beryx.viewreka.settings.ProjectSettingsManager
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer
/**
 * Provides methods for creating an FxProject from a project script passed as resource, URI or {@link GroovyCodeSource}.
 */
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
        FxProjectImpl project = new FxProjectImpl()
        project.scriptText = scriptSource.scriptText
        try {
            if(scriptSource.url?.protocol == 'file') {
                def scriptFile = new File(scriptSource.url.file)
                project.name = FilenameUtils.getBaseName(scriptFile.name)
                project.projectSettingsManager = new ProjectSettingsManager(scriptFile.parent ?: '.', "${project.name}.settings.xml")
                project.scriptFile = scriptFile
            } else {
                project.name = 'noName'
                project.projectSettingsManager = new ProjectSettingsManager(null, null)
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

            def gcl = new ViewrekaClassLoader()
            project.bundles = gcl.configureScript(scriptSource)
            log.info("Bundles: $project.bundles")
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
