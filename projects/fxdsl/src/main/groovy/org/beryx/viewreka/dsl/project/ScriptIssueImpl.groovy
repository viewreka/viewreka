package org.beryx.viewreka.dsl.project

import org.beryx.viewreka.fxui.ScriptIssue
import org.codehaus.groovy.syntax.SyntaxException

/**
 * Implementation of the {@link ScriptIssue} interface.
 */
public class ScriptIssueImpl implements ScriptIssue {
    def Throwable error
    def boolean fatal = true
    def String message
    def String sourceLocator = null
    def int line = -1
    def int column = -1

    public static ScriptIssue fromThrowable(Throwable t) {
        ScriptIssueImpl errInfo = new ScriptIssueImpl()
        errInfo.error = t
        errInfo.message = t.message
        if(t instanceof SyntaxException) {
            SyntaxException se = (SyntaxException) t
            errInfo.fatal = se.fatal
            errInfo.sourceLocator = se.sourceLocator
            errInfo.line = se.startLine
            errInfo.column = se.startColumn
        }
        return errInfo
    }

    @Override
    public String toString() {
        if(error) {
            StringWriter sw = new StringWriter();
            if(line > 0) sw << "Line $line, column $column: "

            error.printStackTrace(new PrintWriter(sw))

//			sw << error.getClass().name << (message ? ": $message" : "") << '\n'
//			StackTraceUtils.printSanitizedStackTrace(error, new PrintWriter(sw) {
//				void println(String s) { super.println('\t' + s) }
//			})
            return sw.toString();
        } else {
            return message;
        }
    }
}
