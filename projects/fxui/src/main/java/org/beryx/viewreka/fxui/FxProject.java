package org.beryx.viewreka.fxui;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.beryx.viewreka.project.Project;

/**
 * An extension of the {@link Project} interface for JavaFX Viewreka projects.
 */
public interface FxProject extends Project<FxView>{
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
