/**
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
package org.beryx.viewreka.fxui;

/**
 * An issue detected while interpreting the project script
 */
public interface ScriptIssue {
    /**
     * @return a text describing this issue
     */
    String getMessage();

    /**
     * @return true, if this issue prevents the construction of a valid Viewreka project
     */
    boolean isFatal();

    /**
     * @return a (possibly null) {@link Throwable}, which is the cause of this issue
     */
    Throwable getError();

    /**
     * @return a (possibly null) string used to locate the source of the project script
     */
    String getSourceLocator();

    /**
     * @return the line on which the issue occurred
     */
    int getLine();

    /**
     * @return the column on which the issue occurred
     */
    int getColumn();
}
