package org.beryx.viewreka.dsl.helper

import groovy.util.logging.Slf4j
import org.beryx.viewreka.core.ViewrekaException
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * An ASTTransformation that uses a TraceVisitor to collect information about the AST of its script
 */
@Slf4j
@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class TraceTransformation extends AbstractASTTransformation {

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        try {
            assert nodes?.length >= 2
            assert nodes[0] instanceof AnnotationNode
            assert nodes[1] instanceof ClassNode

            ClassNode clsNode = nodes[1]
            MethodNode runMethod = clsNode.getDeclaredMethods("run").find { m -> !m.parameters }
            assert runMethod

            log.trace "Class ${runMethod.declaringClass} / Method ${runMethod.name} (${runMethod.parameters}):\n${runMethod.code.text}"
            assert source.configuration instanceof KeywordParser.CompConfig
            def visitor = new TraceVisitor(source.configuration.traces)
            runMethod.code.visit(visitor)
        } catch(Exception e) {
            throw new ViewrekaException("Cannot trace the script ${source.name}", e)
        }

    }
}
