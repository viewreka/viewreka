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
package org.beryx.viewreka.bundle.api;

import org.beryx.viewreka.model.ProjectModel;

import java.util.List;

/**
 * Code templates add support for creating code fragments and inserting them into Viewreka scripts.
 * <br/>The CodeTemplate interface is GUI agnostic, but non-trivial implementations are usually associated with a particular GUI framework (for example, JavaFx).
 * <p/>The following scenario describes the typical use case for code templates:<ul>
 * <li>A code editor displays a context menu containing a series of code templates allowed in the current code context
 * (that is, code templates that can generate code fragments allowed at the current caret position).</li>
 * <li>When the user selects a code template, a dialog is displayed, which allows the user to set diverse parameters required for generating the code fragment.</li>
 * <li>After closing the dialog, a code fragment is created and inserted at the current caret position.</li>
 * </ul>
 */
public interface CodeTemplate {
    public static interface CodeFragment {
        String getCode();

        /**
         * @return the offset in the text returned by {@link #getCode()} at which the caret should be positioned after inserting the code fragment.
         *         -1 means the caret should be positioned after the last character of the code fragment.
         */
        int getCaretPosition();
    }

    public static interface Context {
        /** @return a keyword chain describing the code context at the current caret position */
        List<String> getKeywordChain();
    }

    public static interface Configuration {
        ProjectModel<?> getProjectModel();
        Context getContext();
    }

    /** @return the name of this code template, which will be displayed in the code area's context menu */
    String getName();

    /** @return the description of this code template */
    String getDescription();

    /** @return a sample code for this template */
    String getSample();

    /** @return the keyword associated with this code template */
    String getKeyword();

    /** @return true, if this template is allowed in the code context provided as argument */
    boolean isAllowedInContext(Context context);

    /**
     * Provides a {@link CodeFragment} for the given configuration.
     * <br/>Typically, this method will display a dialog allowing to set diverse parameters required for generating the code fragment.
     */
    CodeFragment getCodeFragment(Configuration configuration);
}
