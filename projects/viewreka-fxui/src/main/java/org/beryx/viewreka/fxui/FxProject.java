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

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.beryx.viewreka.project.Project;

/**
 * An extension of the {@link Project} interface for JavaFX Viewreka projects.
 */
public interface FxProject extends Project<FxView, FxGui>{
    /**
     * @return the text of the project script
     */
    String getScriptText();

    /**
     * @return the {@link URI} of the project script. May be null, if the script has not been retrieved using a URI.
     */
    URI getScriptUri();

    /**
     * @return true, if modifications of the project script can be saved. This is typically the case when the script source is a writable file.
     */
    boolean canSaveScript();

    /**
     * Saves the specified text as project script.
     * @param scriptText the text to be saved as project script
     * @return true, if the text has been successfully saved;
     * false should be returned only if {@link #canSaveScript()} returns false, while other problems should be reported by throwing an exception.
     * @throws IOException if the save operation failed
     */
    boolean saveScript(String scriptText) throws IOException;

    /**
     * @return a list of issues detected while interpreting the project script
     */
    List<ScriptIssue> getScriptIssues();
}
