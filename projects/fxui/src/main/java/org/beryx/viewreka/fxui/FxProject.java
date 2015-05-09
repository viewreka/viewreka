package org.beryx.viewreka.fxui;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.beryx.viewreka.project.Project;

public interface FxProject extends Project<FxView>{
	boolean isValid();
	String getScriptText();
	/** May be null */
	URI getScriptUri();
	boolean canSaveScript();
	boolean saveScript(String scriptText) throws IOException;
	List<ScriptIssue> getScriptIssues();
}
