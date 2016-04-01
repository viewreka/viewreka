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
