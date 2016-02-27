package org.beryx.viewreka.fxapp;

import javafx.collections.ObservableMap;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.dsl.LibDirProvider;
import org.beryx.viewreka.fxui.settings.FxPropsAwareDialog;
import org.beryx.viewreka.fxui.settings.FxPropsManager;
import org.beryx.viewreka.fxui.settings.GuiSettings;
import org.beryx.viewreka.settings.SettingsManager;
import org.controlsfx.dialog.Wizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * The dialog used to create a new project.
 */
public class NewProject implements FxPropsAwareDialog<File> {
    private static final Logger log = LoggerFactory.getLogger(NewProject.class);

    private final SettingsManager<GuiSettings> guiSettingsManager;

    private final Wizard wizard;
    private final ProjectBasicInfo basicInfo;
    private final ProjectLibs projectLibs;

    public NewProject(SettingsManager<GuiSettings> guiSettingsManager) {
        this.guiSettingsManager = guiSettingsManager;

        this.wizard = new Wizard();
        wizard.setTitle("Create new project");
        this.basicInfo = ProjectBasicInfo.createWith(guiSettingsManager);
        this.projectLibs = ProjectLibs.createWith(guiSettingsManager) ;
        wizard.setFlow(new Wizard.LinearFlow(basicInfo.createWizardPage(), projectLibs.createWizardPage()));
    }

    @Override
    public FxPropsManager getFxPropsManager() {
        return new FxPropsManager(guiSettingsManager.getSettings(), "newProject");
    }

    @Override
    public Optional<File> showAndWait() {
        Optional<ButtonType> optButton = wizard.showAndWait();
        if(optButton.isPresent()) {
            if (optButton.get() == ButtonType.FINISH) {
                ObservableMap<String, Object> settings = wizard.getSettings();
                System.out.println("Wizard finished, settings: " + settings);

                String prjName = (String)settings.get(ProjectBasicInfo.PROJECT_NAME_KEY);
                String prjDir = (String)settings.get(ProjectBasicInfo.PROJECT_DIR_KEY);
                ListView<ProjectLibs.LibListEntry> lstLib = (ListView<ProjectLibs.LibListEntry>)settings.get(ProjectLibs.LIB_LIST_KEY);
                File projectFile = createProject(prjName, prjDir, lstLib);
                if(projectFile != null) {
                    return Optional.of(projectFile);
                }
            }
        }
        return Optional.empty();
    }

    public Dialog<File> getDialog() {
        // TODO - replace this hack
        try {
            Field dialogField = Wizard.class.getDeclaredField("dialog");
            dialogField.setAccessible(true);
            return (Dialog)dialogField.get(wizard);
        } catch (Exception e) {
            throw new ViewrekaException("Cannot retrieve the dialog field", e);
        }
    }

    private File createProject(String prjName, String prjDirPath, ListView<ProjectLibs.LibListEntry> lstLib) {
        File prjFile = basicInfo.createProject();
        if(prjFile == null) return null;
        File prjDir = prjFile.getParentFile();
        projectLibs.installLibs(prjName, prjDir);
        return prjFile;
    }

}
