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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.beryx.viewreka.bundle.repo.BundleInfo;
import org.beryx.viewreka.bundle.repo.BundleReader;
import org.beryx.viewreka.core.Version;
import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.dsl.LibDirProvider;
import org.beryx.viewreka.fxcommons.Dialogs;
import org.beryx.viewreka.fxcommons.FXMLNode;
import org.beryx.viewreka.fxui.settings.FxPropsAwareWindow;
import org.beryx.viewreka.fxui.settings.FxPropsManager;
import org.beryx.viewreka.fxui.settings.GuiSettings;
import org.beryx.viewreka.settings.SettingsManager;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The dialog used to choose project libraries (jars and vbundles)
 */
public class ProjectLibs extends BorderPane implements FXMLNode, FxPropsAwareWindow {
    private static final Logger log = LoggerFactory.getLogger(ProjectLibs.class);

    public static final String LIB_LIST_KEY = "libListKey";

    public static final String PROP_LAST_LIBRARY_DIR= "newProject.last.library.dir";

    private final SettingsManager<GuiSettings> guiSettingsManager;

    @FXML private ListView<LibListEntry> lstLib;
    @FXML private Button butBundleAdd;
    @FXML private Button butUncatalogedLibAdd;
    @FXML private Button butLibRemove;

    @FXML private ListView<String> lstExistingLib;

    private final List<Pair<String, Version>> existingBundles = new ArrayList<>();
    private class Page extends WizardPane {
        Page() {
            setContent(ProjectLibs.this);
        }

        @Override
        public void onEnteringPage(Wizard wizard) {
            // TODO - move the following line to onExistingPage() when issue #415 (https://bitbucket.org/controlsfx/controlsfx/issues/415/wizard-doesnt-extract-values-from-last) has been fixed.
            wizard.getSettings().put(LIB_LIST_KEY, lstLib);

            String projectDir = (String)wizard.getSettings().get(ProjectBasicInfo.PROJECT_DIR_KEY);
            initExistingLibs(projectDir);
            super.onEnteringPage(wizard);
        }
    }

    protected void initExistingLibs(String projectDir) {
        File[] libs = null;
        existingBundles.clear();
        lstExistingLib.getItems().clear();
        if(projectDir != null) {
            File projectLibDir = LibDirProvider.getLibDir(new File(projectDir));
            libs = projectLibDir.listFiles(f -> f.isFile() && (f.getName().endsWith(".jar") || f.getName().endsWith(".vbundle")));
            if((libs != null) && (libs.length > 0)) {
                for(File f : libs) {
                    String fName = f.getName();
                    try {
                        BundleInfo info = (BundleInfo)((List<?>) new BundleReader().loadBundle(f.toURI().toURL())).get(1);
                        if(info != null) {
                            existingBundles.add(new ImmutablePair<>(info.getId(), info.getVersion()));
                            fName += " (" + info.getName() + ")";
                        }
                    } catch (MalformedURLException e) {
                        log.error("Cannot read bundle from " + f, e);
                    }
                    lstExistingLib.getItems().add(fName);
                }
            }
        }
    }

    public static interface LibListEntry {
        String getCellText();
        void copyTo(Path destDirPath);
        default String getTooltipText() { return null; }
        default BundleInfo getBundleInfo() { return null; }

        static LibListEntry forFilePath(String path) {
            return new LibListEntry() {
                @Override public String getCellText() { return path; }
                @Override public void copyTo(Path destDirPath) {
                    Path libPath = Paths.get(new File(path).toURI().normalize());
                    try {
                        Path targetLibPath = destDirPath.resolve(libPath.getFileName());
                        Files.copy(libPath, targetLibPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                    } catch(Exception e) {
                        throw new ViewrekaException("Cannot copy '" + path + "' to '" + destDirPath + "'.", e);
                    }
                }
                @Override public String toString() { return path; }
            };
        }

        static LibListEntry forBundleInfo(BundleInfo bundleInfo) {
            return new LibListEntry() {
                @Override public String getCellText() { return bundleInfo.getName() + " " + bundleInfo.getVersion() + " (" + bundleInfo.getFileName() + ")"; }
                @Override public void copyTo(Path destDirPath) {
                    try (InputStream is = new URL(bundleInfo.getUrl()).openStream()) {
                        Path targetLibPath = destDirPath.resolve(bundleInfo.getFileName());
                        Files.copy(is, targetLibPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception e) {
                        throw new ViewrekaException("Cannot download '" + bundleInfo.getUrl() + "' to '" + destDirPath + "'.", e);
                    }
                }
                @Override public String getTooltipText() { return bundleInfo.getDescription(); }
                @Override public BundleInfo getBundleInfo() { return bundleInfo; }
                @Override public String toString() { return bundleInfo.getId(); }
            };
        }
    }

    public static ProjectLibs createWith(SettingsManager<GuiSettings> guiSettingsManager) {
        return new ProjectLibs(guiSettingsManager).load();
    }

    private ProjectLibs(SettingsManager<GuiSettings> guiSettingsManager) {
        this.guiSettingsManager = guiSettingsManager;
    }

    public WizardPane createWizardPage() {
        return new Page();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        check("lstLib", lstLib);
        check("butBundleAdd", butBundleAdd);
        check("butUncatalogedLibAdd", butUncatalogedLibAdd);
        check("butLibRemove", butLibRemove);
        check("lstExistingLib", lstExistingLib);

        lstLib.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        BooleanBinding libRemoveBinding = Bindings.createBooleanBinding(() -> lstLib.getSelectionModel().isEmpty(), lstLib.getSelectionModel().selectedItemProperty());
        butLibRemove.disableProperty().bind(libRemoveBinding);

        lstLib.setCellFactory(new Callback<ListView<LibListEntry>, ListCell<LibListEntry>>() {
            @Override public ListCell<LibListEntry> call(ListView<LibListEntry> entry) {
                return new ListCell<LibListEntry>() {
                    @Override public void updateItem(LibListEntry item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setText(item.getCellText());
                            String tooltipText = item.getTooltipText();
                            if(tooltipText != null && !tooltipText.isEmpty()) {
                                setTooltip(new Tooltip(tooltipText));
                            }
                        }
                    }
                };
            }
        });
    }

    @Override
    public FxPropsManager getFxPropsManager() {
        return new FxPropsManager(guiSettingsManager.getSettings(), "newProject");
    }

    public void addBundles() {
        List<BundleInfo> bundleEntries = lstLib.getItems().stream()
                .map(libEntry -> libEntry.getBundleInfo())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        BundleChooser bundleChooser = BundleChooser.createWith(guiSettingsManager, bundleEntries, existingBundles);
        bundleChooser.showDialog("Viewreka Bundles");

        List<LibListEntry> uncataloged = lstLib.getItems().stream().filter(entry -> entry.getBundleInfo() == null).collect(Collectors.toList());
        List<LibListEntry> bundles = bundleChooser.getSelectedBundlesMap().values().stream().map(LibListEntry::forBundleInfo).collect(Collectors.toList());
        ObservableList<LibListEntry> items = lstLib.getItems();
        items.clear();
        items.addAll(bundles);
        items.addAll(uncataloged);
    }

    public void addUncatalogedLibs() {
        FileChooser libChooser = new FileChooser();
        libChooser.setTitle("Select libraries");
        libChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("jar and vbundle files", "*.jar", "*.vbundle"),
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

            Stream<String> libStream = libFiles.stream().map(f -> f.getAbsolutePath())
                    .filter(s -> (s != null && !s.isEmpty()))
                    .filter(s -> lstLib.getItems().stream().map(entry -> entry.getCellText()).noneMatch(txt -> txt.equals(s)));
            libStream.forEach(s -> lstLib.getItems().add(LibListEntry.forFilePath(s)));
        }
    }

    public void removeLibs() {
        List<LibListEntry> selectedItems = new ArrayList<>(lstLib.getSelectionModel().getSelectedItems());
        selectedItems.forEach(item -> lstLib.getItems().remove(item));
    }


    public void installLibs(String prjName, File prjDir) {
        InstallLibsTask task = new InstallLibsTask(prjName, prjDir, lstLib);

        Stage progressStage = new Stage();
        progressStage.setOnCloseRequest(ev -> {
            if(Dialogs.confirmYesNo("Cancel", "Are you sure you want to cancel the installation of project libraries?", null)) {
                task.cancel();
            }
        });
        progressStage.initStyle(StageStyle.UTILITY);
        progressStage.initModality(Modality.APPLICATION_MODAL);
        progressStage.setTitle("Create project " + prjName);

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(24, 10, 0, 24));

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(64, 64);
        progressIndicator.setMinSize(64, 64);
        progressIndicator.setMaxSize(64, 64);
//        progressIndicator.progressProperty().bind(task.progressProperty());
        grid.add(progressIndicator, 0, 0);

        Label actionLabel = new Label("Install project libraries...");
        actionLabel.textProperty().bind(task.messageProperty());
        grid.add(actionLabel, 1, 0);

        progressStage.setScene(new Scene(grid, 600, 120));

        task.setOnSucceeded(ev -> closeProgress(progressStage, task));
        task.setOnFailed(ev -> closeProgress(progressStage, task));
        task.setOnCancelled(ev -> closeProgress(progressStage, task));

        progressStage.setOnShown(ev -> Executors.newSingleThreadExecutor().submit(task));
        progressStage.showAndWait();
    }


    private void closeProgress(Stage stage, InstallLibsTask task) {
        stage.close();

        Worker.State state = task.getState();
        List<Pair<String,Throwable>> problems = task.getProblems();
        if(problems.isEmpty() && state == Worker.State.SUCCEEDED) return;
        String message = (state == Worker.State.CANCELLED) ? "Installation cancelled" : "Problems occurred during the installation";
        Throwable t = task.getException();
        if(!problems.isEmpty()) {
            message += ":\n\t" + problems.stream().map(pair -> pair.getKey()).collect(Collectors.joining("\n\t"));
            if(t == null) {
                t = problems.get(0).getValue();
            }
        }
        Dialogs.error("Project created with problems", message, t);
    }

    private static class InstallLibsTask extends Task<Void> {
        private final String prjName;
        private final File prjDir;
        private final ListView<ProjectLibs.LibListEntry> lstLib;

        private final List<Pair<String,Throwable>> problems = new ArrayList<>();

        InstallLibsTask(String prjName, File prjDir, ListView<ProjectLibs.LibListEntry> lstLib) {
            this.prjName = prjName;
            this.prjDir = prjDir;
            this.lstLib = lstLib;
        }

        public List<Pair<String, Throwable>> getProblems() {
            return problems;
        }

        @Override
        protected Void call() {
            problems.clear();
            long totalActions = 1 + lstLib.getItems().size();
            File prjLibDir = LibDirProvider.getLibDir(prjDir);
            Path libDirPath = Paths.get(prjLibDir.toURI().normalize());
            for(int i = 0; i < lstLib.getItems().size(); i++) {
                if(isCancelled()) return null;
                ProjectLibs.LibListEntry libListEntry = lstLib.getItems().get(i);
                try {
                    updateProgress(i, totalActions);
                    updateMessage("Installing " + libListEntry.getCellText() + "...");
                    libListEntry.copyTo(libDirPath);
                } catch (Exception e) {
                    if(isCancelled()) return null;
                    Throwable t = e;
                    if (e instanceof ViewrekaException && e.getCause() != null) {
                        t = e.getCause();
                    }
                    problems.add(new ImmutablePair<>("Failed to install " + libListEntry.getCellText(), t));
                }
            }
            if(isCancelled()) return null;
            updateProgress(lstLib.getItems().size(), totalActions);
            updateMessage("Creating CSS file...");
            try (InputStream sourceStream = getClass().getResourceAsStream("/css/viewreka-default.css")) {
                File cssFile = new File(prjDir, prjName + ".css");
                Path cssTargetPath = Paths.get(cssFile.toURI().normalize());
                Files.copy(sourceStream, cssTargetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                problems.add(new ImmutablePair<>("Failed to create the CSS file '" + (prjName + ".css") + "'", e));
            }
            updateProgress(totalActions, totalActions);
            updateMessage("Done");

            return null;
        }
    }

}
