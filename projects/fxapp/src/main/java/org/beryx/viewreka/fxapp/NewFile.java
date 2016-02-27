package org.beryx.viewreka.fxapp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import org.beryx.viewreka.fxcommons.Dialogs;
import org.beryx.viewreka.fxcommons.FXMLNode;
import org.beryx.viewreka.fxui.settings.FxPropsAwareWindow;
import org.beryx.viewreka.fxui.settings.FxPropsManager;
import org.beryx.viewreka.fxui.settings.GuiSettings;
import org.beryx.viewreka.settings.SettingsManager;

/**
 * The dialog used to create a new file.
 */
public class NewFile extends BorderPane implements FXMLNode, FxPropsAwareWindow {
    private final SettingsManager<GuiSettings> guiSettingsManager;

    private File createdFile = null;

    @FXML private TextField txtNewFileName;
    @FXML private TextField txtNewFileDir;
    @FXML private Button butNewFileDir;

    @FXML private Button butNewFileOk;
    @FXML private Button butNewFileCancel;

    public static NewFile createWith(SettingsManager<GuiSettings> guiSettingsManager) {
        return new NewFile(guiSettingsManager).load();
    }

    private NewFile(SettingsManager<GuiSettings> guiSettingsManager) {
        this.guiSettingsManager = guiSettingsManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        check("txtNewFileName", txtNewFileName);
        check("txtNewFileDir", txtNewFileDir);
        check("butNewFileDir", butNewFileDir);

        check("butNewFileOk", butNewFileOk);
        check("butNewFileCancel", butNewFileCancel);

        BooleanBinding fileNameBinding = Bindings.createBooleanBinding(() -> isPathValid(txtNewFileName.getText()), txtNewFileName.textProperty());
        BooleanBinding fileDirBinding = Bindings.createBooleanBinding(() -> isPathValid(txtNewFileDir.getText()), txtNewFileDir.textProperty());
        butNewFileOk.disableProperty().bind(fileNameBinding.and(fileDirBinding).not());

        GuiSettings guiSettings = guiSettingsManager.getSettings();
        txtNewFileDir.setText(guiSettings.getMostRecentProjectDir().getAbsolutePath());
    }

    @Override
    public FxPropsManager getFxPropsManager() {
        return new FxPropsManager(guiSettingsManager.getSettings(), "newFile");
    }

    public void chooseFileDir() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Parent Directory");

        GuiSettings guiSettings = guiSettingsManager.getSettings();
        dirChooser.setInitialDirectory(guiSettings.getMostRecentProjectDir());

        File parentDir = dirChooser.showDialog(getScene().getWindow());
        if(parentDir != null && parentDir.isDirectory()) {
            txtNewFileDir.setText(parentDir.getAbsolutePath());
        }
    }

    public static boolean isPathValid(String path) {
        if(path == null || path.isEmpty()) return false;
        try {
            new File(path).getCanonicalPath();
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    public void createFile() {
        createdFile = null;

        String fileName = txtNewFileName.getText();
        if(!isPathValid(fileName)) return;
        String parentDirPath = txtNewFileDir.getText();
        if(!isPathValid(parentDirPath)) return;

        File fileDir = new File(parentDirPath);
        if(fileDir.isFile()) {
            Dialogs.error("New File error", "Cannot create the file '" + fileName + "' in " + parentDirPath,
                    "A file with the same name already exists.", null);
            return;
        }

        fileDir.mkdirs();
        if(!fileDir.isDirectory()) {
            Dialogs.error("New File error", "Cannot create directory '" + parentDirPath + "'.");
            return;
        }

        File newFile = new File(fileDir, fileName);
        if(newFile.isFile()) {
            Dialogs.error("New File error", "The file '" + newFile.getAbsolutePath() + "' already exists.");
            return;
        }
        try {
            newFile.createNewFile();
        } catch(IOException e) {
            Dialogs.error("New File error", "Cannot create the file '" + newFile.getAbsolutePath() + "'.", e);
            return;
        }

        createdFile = newFile;

        ((Stage)getScene().getWindow()).close();
    }

    public void cancelFile() {
        ((Stage)getScene().getWindow()).close();
    }

    public File getCreatedFile() {
        return createdFile;
    }
}
