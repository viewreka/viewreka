package org.beryx.viewreka.fxapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.beryx.viewreka.fxui.Dialogs;
import org.beryx.viewreka.fxui.FXMLControl;
import org.beryx.viewreka.fxui.settings.GuiSettings;
import org.beryx.viewreka.settings.SettingsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewProject extends BorderPane implements FXMLControl {
	private static final Logger log = LoggerFactory.getLogger(NewProject.class);

	public static final String PROP_LAST_LIBRARY_DIR= "newProject.last.library.dir";
	private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z]\\w*");

	private final SettingsManager<GuiSettings> guiSettingsManager;

	private File createdProjectFile = null;

	@FXML private TextField txtNewProjectName;
	@FXML private TextField txtNewProjectDir;
	@FXML private Button butNewProjectDir;

	@FXML private ListView<String> lstLib;
	@FXML private Button butLibAdd;
	@FXML private Button butLibRemove;

	@FXML private Button butNewProjectOk;
	@FXML private Button butNewProjectCancel;


	public static NewProject createWith(SettingsManager<GuiSettings> guiSettingsManager) {
		return new NewProject(guiSettingsManager).load();
	}

	private NewProject(SettingsManager<GuiSettings> guiSettingsManager) {
		this.guiSettingsManager = guiSettingsManager;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		check("txtNewProjectName", txtNewProjectName);
		check("txtNewProjectDir", txtNewProjectDir);
		check("butNewProjectDir", butNewProjectDir);

		check("lstLib", lstLib);
		check("butLibAdd", butLibAdd);
		check("butLibRemove", butLibRemove);

		check("butNewProjectOk", butNewProjectOk);
		check("butNewProjectCancel", butNewProjectCancel);

		BooleanBinding projectNameBinding = Bindings.createBooleanBinding(() -> isProjectNameValid(txtNewProjectName.getText()), txtNewProjectName.textProperty());
		BooleanBinding projectDirBinding = Bindings.createBooleanBinding(() -> isDirPathValid(txtNewProjectDir.getText()), txtNewProjectDir.textProperty());
		butNewProjectOk.disableProperty().bind(projectNameBinding.and(projectDirBinding).not());

		lstLib.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		BooleanBinding libRemoveBinding = Bindings.createBooleanBinding(() -> lstLib.getSelectionModel().isEmpty(), lstLib.getSelectionModel().selectedItemProperty());
		butLibRemove.disableProperty().bind(libRemoveBinding);
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

	private static String toProjectName(String rawName) {
		if(rawName == null) return null;
		String prjName = rawName;
		prjName = prjName.replaceAll("[^\\w]+", "_");
		while(prjName.startsWith("_")) {
			prjName = prjName.substring(1);
		}
		return prjName;
	}

	public void addLibs() {
		FileChooser libChooser = new FileChooser();
		libChooser.setTitle("Select libraries");
		libChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JAR files", "*.jar"),
                new FileChooser.ExtensionFilter("All files", "*.*")
            );

		GuiSettings guiSettings = guiSettingsManager.getSettings();
		File initialDir = guiSettings.getMostRecentProjectDir();
		String lastLibDirPath = guiSettings.getProperty(PROP_LAST_LIBRARY_DIR, null, true);
		if(lastLibDirPath != null) {
			try {
				File dir = new File(lastLibDirPath);
				if(dir.isDirectory()) {
					initialDir = dir;
				}
			} catch(Exception e) {
				log.warn("Cannot retrieve last library path", e);
			}
		}
		libChooser.setInitialDirectory(initialDir);
		List<File> libFiles = libChooser.showOpenMultipleDialog(getScene().getWindow());
		if(libFiles != null && !libFiles.isEmpty()) {
			guiSettings.setProperty(PROP_LAST_LIBRARY_DIR, libFiles.get(0).getParent());
			Stream<String> libStream = libFiles.stream().map(f -> f.getAbsolutePath()).filter(s -> (s != null && !s.isEmpty()));
			String[] libPaths = libStream.filter(s -> !lstLib.getItems().contains(s)).toArray(String[]::new);
			lstLib.getItems().addAll(libPaths);
		}
	}

	public void removeLibs() {
		List<String> selectedItems = new ArrayList<>(lstLib.getSelectionModel().getSelectedItems());
		selectedItems.forEach(item -> lstLib.getItems().remove(item));
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
		return true;
	}

	public void createProject() {
		createdProjectFile = null;

		String prjName = txtNewProjectName.getText();
		if(!isProjectNameValid(prjName)) return;
		String prjDirPath = txtNewProjectDir.getText();
		if(!isDirPathValid(prjDirPath)) return;

		File prjDir = new File(prjDirPath);
		if(prjDir.isFile()) {
			Dialogs.error("New Project error", "Cannot create the project in " + prjDirPath,
					"A file with the same name as the project directory already exists.", null);
			return;
		}
		if(prjDir.isDirectory() && prjDir.list().length > 0 && !Dialogs.confirm(
				"New Project",
				"The project directory already exists and is not empty.",
				"Some of the files in this directory may be overridden while creating your new project.\n" +
				   "Press '" + ButtonType.OK.getText() + "' to create the project in this directory or '" + ButtonType.CANCEL.getText() + "' to choose another directory.")) {
			return;
		}

		prjDir.mkdirs();
		if(!prjDir.isDirectory()) {
			Dialogs.error("New Project error", "Cannot create project directory '" + prjDirPath + "'.");
			return;
		}

		String prjFileName = prjName + ".viewreka";
		File prjFile = new File(prjDir, prjFileName);
		if(prjFile.isFile()) {
			Dialogs.error("New Project error", "The project file '" + prjFile.getAbsolutePath() + "' already exists.");
			return;
		}
		try {
			prjFile.createNewFile();
		} catch(IOException e) {
			Dialogs.error("New Project error", "Cannot create the project file '" + prjFile.getAbsolutePath() + "'.", e);
			return;
		}

		File prjLibDir = new File(prjDir, "lib");
		if(prjLibDir.isFile()) {
			Dialogs.error("New Project error", "Cannot create the 'lib' directory in '" + prjDirPath + "'.",
					"A file with the name 'lib' exists in the project directory.", null);
			return;
		}
		prjLibDir.mkdirs();
		if(!prjLibDir.isDirectory()) {
			Dialogs.error("New Project error", "Cannot create the 'lib' directory in '" + prjDirPath + "'.");
			return;
		}

		Path libDirPath = Paths.get(prjLibDir.toURI().normalize());
		lstLib.getItems().forEach(lib -> {
			Path libPath = Paths.get(new File(lib).toURI().normalize());
			try {
				Path targetLibPath = libDirPath.resolve(libPath.getFileName());
				Files.copy(libPath, targetLibPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			} catch(Exception e) {
				Dialogs.alert(AlertType.WARNING, "New Project warning", "Cannot copy '" + lib + "' to '" + prjLibDir.getAbsolutePath() + "'.", null, e);
			}
		});

		try(InputStream sourceStream = getClass().getResourceAsStream("/css/viewreka-default.css")) {
			File cssFile = new File(prjDir, prjName + ".css");
			Path cssTargetPath = Paths.get(cssFile.toURI().normalize());
			Files.copy(sourceStream, cssTargetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch(IOException e) {
			Dialogs.alert(AlertType.WARNING, "New Project warning", "Cannot create the CSS file '" + (prjName + ".css") + "'.", null, e);
		}

		createdProjectFile = prjFile;

		((Stage)getScene().getWindow()).close();
	}

	public void cancelProject() {
		((Stage)getScene().getWindow()).close();
	}

	public File getCreatedProjectFile() {
		return createdProjectFile;
	}
}
