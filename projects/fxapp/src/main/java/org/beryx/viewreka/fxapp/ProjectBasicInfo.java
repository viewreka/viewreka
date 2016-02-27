package org.beryx.viewreka.fxapp;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.beryx.viewreka.dsl.LibDirProvider;
import org.beryx.viewreka.fxcommons.Dialogs;
import org.beryx.viewreka.fxcommons.FXMLNode;
import org.beryx.viewreka.fxui.settings.FxPropsAwareWindow;
import org.beryx.viewreka.fxui.settings.FxPropsManager;
import org.beryx.viewreka.fxui.settings.GuiSettings;
import org.beryx.viewreka.settings.SettingsManager;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * The for used to retrieve the name and the home directory of a new project.
 */
public class ProjectBasicInfo extends BorderPane implements FXMLNode, FxPropsAwareWindow {
    private static final Logger log = LoggerFactory.getLogger(ProjectBasicInfo.class);

    public static final String PROJECT_NAME_KEY = "projectNameKey";
    public static final String PROJECT_DIR_KEY = "projectDirKey";

    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z]\\w*");

    private final SettingsManager<GuiSettings> guiSettingsManager;

    @FXML private TextField txtNewProjectName;
    @FXML private TextField txtNewProjectDir;
    @FXML private Button butNewProjectDir;

    private class Page extends WizardPane {
        private final ValidationSupport vs;
        Page() {
            vs = new ValidationSupport();
            vs.initInitialDecoration();

            vs.registerValidator(txtNewProjectName, Validator.createRegexValidator("Invalid project name", NAME_PATTERN, Severity.ERROR));
            vs.registerValidator(txtNewProjectDir, Validator.<String>createPredicateValidator(path -> isDirPathValid(path), "Invalid home directory"));

            setContent(ProjectBasicInfo.this);
        }

        @Override
        public void onEnteringPage(Wizard wizard) {
            wizard.invalidProperty().unbind();
            wizard.invalidProperty().bind(vs.invalidProperty());
        }

        @Override
        public void onExitingPage(Wizard wizard) {
            wizard.getSettings().put(PROJECT_NAME_KEY, txtNewProjectName.getText());
            wizard.getSettings().put(PROJECT_DIR_KEY, txtNewProjectDir.getText());
            super.onExitingPage(wizard);
        }
    }

    public static ProjectBasicInfo createWith(SettingsManager<GuiSettings> guiSettingsManager) {
        return new ProjectBasicInfo(guiSettingsManager).load();
    }

    private ProjectBasicInfo(SettingsManager<GuiSettings> guiSettingsManager) {
        this.guiSettingsManager = guiSettingsManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        check("txtNewProjectName", txtNewProjectName);
        check("txtNewProjectDir", txtNewProjectDir);
        check("butNewProjectDir", butNewProjectDir);

        Bindings.createBooleanBinding(() -> isProjectNameValid(txtNewProjectName.getText()), txtNewProjectName.textProperty());
        Bindings.createBooleanBinding(() -> isDirPathValid(txtNewProjectDir.getText()), txtNewProjectDir.textProperty());
    }

    public WizardPane createWizardPage() {
        return new Page();
    }

    @Override
    public FxPropsManager getFxPropsManager() {
        return new FxPropsManager(guiSettingsManager.getSettings(), "newProject");
    }

    public void chooseProjectDir() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("New Project Directory");

        GuiSettings guiSettings = guiSettingsManager.getSettings();
        dirChooser.setInitialDirectory(guiSettings.getMostRecentProjectDir());

        File projectDir = dirChooser.showDialog(getScene().getWindow());
        if(projectDir != null && projectDir.isDirectory()) {
            txtNewProjectDir.setText(projectDir.getAbsolutePath());
            String prjName = toProjectName(projectDir.getName());
            if(isProjectNameValid(prjName)) {
                txtNewProjectName.setText(prjName);
            }
        }
    }

    public File createProject() {
        String prjName = txtNewProjectName.getText();
        String prjDirPath = txtNewProjectDir.getText();

        File prjDir = new File(prjDirPath).getAbsoluteFile();
        prjDirPath = prjDir.getAbsolutePath();
        prjDir.mkdirs();
        if(!prjDir.isDirectory()) {
            Dialogs.error("New Project error", "Cannot create project directory '" + prjDirPath + "'.");
            return null;
        }
        File prjFile = new File(prjDir, prjName + ".viewreka");
        try {
            prjFile.createNewFile();
        } catch(IOException e) {
            Dialogs.error("New Project error", "Cannot create the project file '" + prjFile.getAbsolutePath() + "'.", e);
            return null;
        }

        File prjLibDir = LibDirProvider.getLibDir(prjDir);
        if(prjLibDir.isFile()) {
            Dialogs.error("New Project error", "Cannot create the 'lib' directory in '" + prjDirPath + "'.",
                    "A file with the name 'lib' exists in the project directory.", null);
            return null;
        }
        prjLibDir.mkdirs();
        prjLibDir = prjLibDir.getAbsoluteFile();
        if(!prjLibDir.isDirectory()) {
            Dialogs.error("New Project error", "Cannot create the 'lib' directory in '" + prjDirPath + "'.");
            return null;
        }
        return prjFile;
    }

    private static String toProjectName(String rawName) {
        if(rawName == null) return null;
        String prjName = rawName;
        prjName = prjName.replaceAll("[^\\w]+", "_");
        while(prjName.startsWith("_")) {
            prjName = prjName.substring(1);
        }
        return prjName;
    }

    public static boolean isProjectNameValid(String name) {
        if(name == null) return false;
        return NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isDirPathValid(String path) {
        if(path == null || path.isEmpty()) return false;
        try {
            new File(path).getCanonicalPath();
        } catch(Exception e) {
            return false;
        }
        if(new File(path).isFile()) return true;
        return true;
    }
}
