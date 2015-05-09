package org.beryx.viewreka.dsl.project

import static org.beryx.viewreka.core.Util.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.fxui.FxProject;
import org.beryx.viewreka.fxui.FxView;
import org.beryx.viewreka.fxui.ScriptIssue;
import org.beryx.viewreka.model.ProjectModelImpl;
import org.beryx.viewreka.settings.ProjectSettings;
import org.beryx.viewreka.settings.SettingsManager;
import org.codehaus.groovy.runtime.IOGroovyMethods;

public class FxProjectImpl extends ProjectModelImpl<FxView> implements FxProject {
	def boolean valid = false;
	def SettingsManager<ProjectSettings> projectSettingsManager;
	def File scriptFile = null;
	def List<ScriptIssue> scriptIssues = []

	private String initialScriptText = "";

	@Override
	public String getScriptText() {
		if(scriptFile != null && scriptFile.isFile()) {
			try {
				return new String(Files.readAllBytes(Paths.get(scriptFile.getAbsolutePath())));
			} catch(IOException e) {
				throw new ViewrekaException("Cannot retrieve script text", e);
			}
		}
		return initialScriptText;
	}

	public void setScriptText(String scriptText) {
		this.initialScriptText = scriptText;
	}

	@Override
	public void validate() {
		super.validate();
		requireNonNull(projectSettingsManager, "projectSettingsManager");
	}


	@Override
	public URI getScriptUri() {
		if(!scriptFile) return null;
		return scriptFile.toURI().normalize();
	}

	@Override
	public boolean canSaveScript() {
		if(!scriptFile) return false;
		if(scriptFile.isDirectory()) return false;
		if(scriptFile.isFile() && !scriptFile.canWrite()) return false;
		return true;
	}
	@Override
	public boolean saveScript(String scriptText) throws IOException {
		if(!canSaveScript()) return false;
		IOGroovyMethods.withCloseable(new FileWriter(scriptFile)) { writer -> writer << scriptText }
		return true;
	}
}
