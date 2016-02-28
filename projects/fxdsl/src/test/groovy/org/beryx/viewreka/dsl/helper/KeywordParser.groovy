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
package org.beryx.viewreka.dsl.helper
import org.beryx.viewreka.dsl.transform.Keyword
import org.beryx.viewreka.dsl.transform.ViewrekaDSL
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer

/**
 * A parser used to inspect {@link ViewrekaDSL} transformations (i.e., transformations performed by a KeywordVisitor)
 */
class KeywordParser {
    private final String metaInfLocation
    private final List<Keyword> keywords

    static class CompConfig extends CompilerConfiguration {
        List<String> traces = []
    }

    def KeywordParser(String metaInfLocation, List<Keyword> keywords) {
        this.metaInfLocation = metaInfLocation
        this.keywords = keywords
    }

    /**
     * Collects information about the AST of a script, with or without applying ViewrekaDSL transformations.
     */
    List<String> parse(String script, String name, boolean transform) {
        def scriptSource = new GroovyCodeSource(script, "${name}.viewreka", 'test')
        def conf = new CompConfig()

        if(transform) {
            def ASTTransformationCustomizer keywordCustomizer = new ASTTransformationCustomizer(keywords: keywords, ViewrekaDSL)
            conf.addCompilationCustomizers(keywordCustomizer)
        }

        def ASTTransformationCustomizer traceCustomizer = new ASTTransformationCustomizer([:], TraceAnnotation)
        conf.addCompilationCustomizers(traceCustomizer)

        def gcl = new GroovyClassLoader()

        def foobarPath = getClass().getResource(metaInfLocation)
        assert foobarPath
        // Ensure that directory URLs end with a slash and file URLs don't
        foobarPath = new File(foobarPath.toURI()).toURI().toURL()
        gcl.addURL(foobarPath)

        Thread.currentThread().setContextClassLoader(gcl)
        new GroovyShell(gcl, conf).parse(scriptSource)

        return conf.traces
    }
}
