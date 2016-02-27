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
