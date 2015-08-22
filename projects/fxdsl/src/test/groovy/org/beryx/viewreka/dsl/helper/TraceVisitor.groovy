package org.beryx.viewreka.dsl.helper

import groovy.transform.Canonical
import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.expr.MethodCallExpression

/**
 * A CodeVisitor that collects method call information
 */
@Canonical
class TraceVisitor extends CodeVisitorSupport {
    final List<String> traces

    public void visitMethodCallExpression(MethodCallExpression call) {
        traces << call.text
        super.visitMethodCallExpression(call)
    }
}
