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
package org.beryx.viewreka.fxapp;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.beryx.viewreka.fxui.settings.FxPropsAwareDialog;
import org.beryx.viewreka.fxui.settings.FxPropsManager;
import org.beryx.viewreka.fxui.settings.GuiSettings;
import org.beryx.viewreka.settings.SettingsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The dialog used to edit a project.
 */
public class EditProject extends Dialog<ButtonType> implements FxPropsAwareDialog<ButtonType> {
    private static final Logger log = LoggerFactory.getLogger(EditProject.class);

    private final SettingsManager<GuiSettings> guiSettingsManager;

    private final ProjectLibs projectLibs;

    public EditProject(SettingsManager<GuiSettings> guiSettingsManager) {
        this.guiSettingsManager = guiSettingsManager;
        this.projectLibs = ProjectLibs.createWith(guiSettingsManager) ;
        getDialogPane().setContent(projectLibs);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }

    @Override
    public FxPropsManager getFxPropsManager() {
        return new FxPropsManager(guiSettingsManager.getSettings(), "editProject");
    }

    public ProjectLibs getProjectLibs() {
        return projectLibs;
    }
}
