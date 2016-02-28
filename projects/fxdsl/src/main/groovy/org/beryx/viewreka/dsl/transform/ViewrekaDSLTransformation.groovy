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
package org.beryx.viewreka.dsl.transform;

import groovy.util.logging.Slf4j

import org.beryx.viewreka.core.ViewrekaException
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * Performs Viewreka specific AST transformations (keyword handling, alias replacements etc.).
 */
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
                return Class.forName(k.text, true, Thread.currentThread().contextClassLoader).getConstructor().newInstance()
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
