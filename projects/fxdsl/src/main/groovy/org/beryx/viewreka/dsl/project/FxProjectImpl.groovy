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

import org.beryx.viewreka.bundle.api.ViewrekaBundle
import org.beryx.viewreka.core.ViewrekaException
import org.beryx.viewreka.fxui.FxProject
import org.beryx.viewreka.fxui.FxView
import org.beryx.viewreka.fxui.ScriptIssue
import org.beryx.viewreka.model.ProjectModelImpl
import org.beryx.viewreka.settings.ProjectSettings
import org.beryx.viewreka.settings.SettingsManager
import org.codehaus.groovy.runtime.IOGroovyMethods

import java.nio.file.Files
import java.nio.file.Paths

import static org.beryx.viewreka.core.Util.requireNonNull;

/**
 * Implementation of {@link FxProject}.
 */
public class FxProjectImpl extends ProjectModelImpl<FxView> implements FxProject {
    SettingsManager<ProjectSettings> projectSettingsManager;
    File scriptFile = null;
    List<ScriptIssue> scriptIssues = []
    List<ViewrekaBundle> bundles = []

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
