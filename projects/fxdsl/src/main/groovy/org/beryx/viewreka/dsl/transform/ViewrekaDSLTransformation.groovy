package org.beryx.viewreka.dsl.transform;

import groovy.transform.Canonical;
import groovy.transform.Immutable;
import groovy.util.logging.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.beryx.viewreka.core.ViewrekaException;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.GroovyClassVisitor;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@Slf4j
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class ViewrekaDSLTransformation extends AbstractASTTransformation {

	def check(condition, message) {
		if(!condition) throw new ViewrekaException(message)
	}


	@Override
	public void visit(ASTNode[] nodes, SourceUnit source) {
		try {
			check(source.getName().endsWith(".viewreka"), "Not a viewreka file")
			check(nodes?.length >= 2, "Expecting at least 2 nodes")
			check(nodes[0] instanceof AnnotationNode, "Expecting AnnotationNode as nodes[0] but was: ${nodes[0]}")
			check(nodes[1] instanceof ClassNode, "Expecting ClassNode as nodes[1] but was: ${nodes[1]}")

			AnnotationNode annNode = nodes[0]
			Keyword[] keywords = annNode?.members?.get('keywords')?.expressions.collect() { ClassExpression k ->
				log.trace "Keyword class: ${k.text}"
				return Class.forName(k.text).getConstructor().newInstance()
			}
			log.debug "keywords: $keywords"

			ClassNode clsNode = nodes[1]
			MethodNode runMethod = clsNode.getDeclaredMethods("run").find { m -> !m.parameters }
			check(runMethod, "runMethod not found")

			log.trace "Class ${runMethod.declaringClass} / Method ${runMethod.name} (${runMethod.parameters}):\n${runMethod.code.text}"
			def visitor = new KeywordVisitor(keywords)
			runMethod.code.visit(visitor)
		} catch(Exception e) {
			if(e instanceof ViewrekaException) throw e
			throw new ViewrekaException("Cannot transform keywords of script ${source.name}", e)
		}

	}
}
