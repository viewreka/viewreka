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
