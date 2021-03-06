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

import groovy.lang.GroovyCodeSource;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.beryx.viewreka.bundle.api.CodeTemplate;
import org.beryx.viewreka.bundle.api.ViewrekaBundle;
import org.beryx.viewreka.core.VersionInfo;
import org.beryx.viewreka.dsl.ViewrekaClassLoader;
import org.beryx.viewreka.dsl.project.ScriptIssueImpl;
import org.beryx.viewreka.fxapp.codearea.CodeAreaTab;
import org.beryx.viewreka.fxapp.codearea.CodeTabData;
import org.beryx.viewreka.fxapp.codearea.ViewrekaCodeArea;
import org.beryx.viewreka.fxcommons.Dialogs;
import org.beryx.viewreka.fxcommons.FXMLNode;
import org.beryx.viewreka.fxui.FxGui;
import org.beryx.viewreka.fxui.FxProject;
import org.beryx.viewreka.fxui.FxView;
import org.beryx.viewreka.fxui.chart.FxChartBuilder;
import org.beryx.viewreka.fxui.settings.GuiSettings;
import org.beryx.viewreka.fxui.settings.GuiSettingsManager;
import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.project.ProjectReader;
import org.beryx.viewreka.settings.ProjectSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

import static org.beryx.viewreka.fxapp.codearea.CodeTabData.getData;

/**
 * The main pane of a Viewreka GUI.
 */
public class Viewreka extends BorderPane implements FXMLNode, ClassLoaderManager, FxGui {
    private static final Logger log = LoggerFactory.getLogger(Viewreka.class);

    private static final int MAX_TEXT_FILE_LENGTH = 65535;
    private static final int MAX_OPEN_FILES = 12;

    public static final String PROP_FILE_AREA_VISIBLE = "file.area.visible";
    public static final String PROP_MAIN_SPLIT_PANE_DIVIDER_POSITION = "mainSplitPane.divider.position";
    public static final String PROP_FILE_SPLIT_PANE_DIVIDER_POSITION = "fileSplitPane.divider.position";
    public static final String PROP_OPEN_FILE_TABS = "open.file.tabs";
    public static final String PROP_SELECTED_TAB_INDEX = "selected.tab.index";
    public static final String PROP_SOURCE_CODE_CARET_POSITION = "sourceCode.caret.position";
    public static final String PROP_CHART_STYLESHEET = "chart.stylesheet";
    public static final String PROP_CHART_EXPORT_DIR = "chart.export.dir";

    // No valid file name can start with this prefix. Used to avoid collisions with existing file names.
    public static final String FILE_ALIAS_PREFIX = "\u0000";
    public static final String FILE_ALIAS_SOURCE_CODE = FILE_ALIAS_PREFIX + "sourceCode.alias";
    public static final String FILE_ALIAS_HELP = FILE_ALIAS_PREFIX + "help.alias";

    @FXML private SplitPane mainSplitPane;
    @FXML private SplitPane fileSplitPane;
    @FXML private ViewrekaCodeArea sourceCodeArea;
    @FXML private TextArea errorArea;
    @FXML private TabPane projectTabPane;
    @FXML private TabPane viewsTabPane;

    @FXML private MenuItem mnuNewProject;
    @FXML private Button butNewProject;
    @FXML private MenuItem mnuOpenProject;
    @FXML private Button butOpenProject;
    @FXML private MenuItem mnuEditProject;

    @FXML private MenuItem mnuNewFile;
    @FXML private Button butNewFile;
    @FXML private MenuItem mnuOpenFile;
    @FXML private Button butOpenFile;

    @FXML private MenuItem mnuSaveFile;
    @FXML private Button butSaveFile;
    @FXML private MenuItem mnuSaveAll;
    @FXML private Button butSaveAll;

    @FXML private MenuItem mnuCloseTab;
    @FXML private MenuItem mnuCloseProject;

    @FXML private MenuItem mnuReloadProject;
    @FXML private Button butReloadProject;
    @FXML private MenuItem mnuToggleFilePane;
    @FXML private MenuItem mnuShowSourceCode;
    @FXML private Tab tabSourceCode;
    @FXML private MenuItem mnuShowHelp;
    @FXML private Tab tabHelp;

    @FXML private MenuItem mnuExportChart;
    @FXML private Button butExportChart;
    @FXML private MenuItem mnuExportVideo;
    @FXML private Button butExportVideo;

    @FXML private MenuItem mnuStylesheet;

    @FXML private WebView helpBrowser;

    private FxProject project;
    private final StringProperty projectPathProperty = new SimpleStringProperty();

    private final ProjectReader<FxProject> projectReader;
    private final GuiSettingsManager guiSettingsManager;

    private final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

    private MenuTabBinding menuTabBindingSourceCode;
    private MenuTabBinding menuTabBindingHelp;

    private static class MenuTabBinding {
        private final TabPane tabPane;
        private final Tab tab;
        private final MenuItem menuItem;
        private final String fileAlias;

        private ProjectSettings settings;

        public MenuTabBinding(TabPane tabPane, Tab tab, MenuItem menuItem, String fileAlias, boolean insertFirst) {
            this.tabPane = tabPane;
            this.tab = tab;
            this.menuItem = menuItem;
            this.fileAlias = fileAlias;
            getData(tab).setFilePath(fileAlias);

            tab.setOnClosed(event -> {
                menuItem.setDisable(false);
            });
            menuItem.setOnAction(ev -> {
                ObservableList<Tab> tabs = tabPane.getTabs();
                if(!tabs.contains(tab)) {
                    if(insertFirst) {
                        tabs.add(0, tab);
                    } else {
                        tabs.add(tab);
                    }
                }
                tabPane.getSelectionModel().select(tab);
                if(settings != null) {
                    List<String> openFiles = getOpenFiles(settings);
                    openFiles.clear();
                    tabs.forEach(t -> openFiles.add(getData(t).getFilePath()));
                }
            });
        }

        public void setSettings(ProjectSettings settings, boolean disabledOnNullSettings) {
            this.settings = null;
            boolean disabled = (settings == null) ? disabledOnNullSettings : (tabPane.getSelectionModel().getSelectedItem() == tab);
            log.debug(menuItem.getId() + " " + (disabled ? "disabled" : "enabled") + ", settings: " + (settings != null)
                                    + ", open files: " + getOpenFiles(settings) + " / fileAlias: " + fileAlias);
            menuItem.setDisable(disabled);
            this.settings = settings;
        }

        public void showTab() {
            menuItem.getOnAction().handle(new ActionEvent());
        }
    }

    public Viewreka(ProjectReader<FxProject> projectReader, GuiSettingsManager guiSettingsManager) {
        this.projectReader = projectReader;
        this.guiSettingsManager = guiSettingsManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        check("mainSplitPane", mainSplitPane);
        check("fileSplitPane", fileSplitPane);
        check("sourceCodeArea", sourceCodeArea);
        check("errorArea", errorArea);
        check("projectTabPane", projectTabPane);
        check("viewsTabPane", viewsTabPane);

        check("mnuNewProject", mnuNewProject);
        check("butNewProject", butNewProject);
        check("mnuOpenProject", mnuOpenProject);
        check("butOpenProject", butOpenProject);
        check("mnuEditProject", mnuEditProject);

        check("mnuNewFile", mnuNewFile);
        check("butNewFile", butNewFile);
        check("mnuOpenFile", mnuOpenFile);
        check("butOpenFile", butOpenFile);

        check("mnuSaveFile", mnuSaveFile);
        check("butSaveFile", butSaveFile);
        check("mnuSaveAll", mnuSaveAll);
        check("butSaveAll", butSaveAll);

        check("mnuCloseTab", mnuCloseTab);
        check("mnuCloseProject", mnuCloseProject);

        check("mnuReloadProject", mnuReloadProject);
        check("butReloadProject", butReloadProject);

        check("mnuToggleFilePane", mnuToggleFilePane);

        check("mnuShowSourceCode", mnuShowSourceCode);
        check("tabSourceCode", tabSourceCode);
        check("mnuShowHelp", mnuShowHelp);
        check("tabHelp", tabHelp);

        check("mnuExportChart", mnuExportChart);
        check("butExportChart", butExportChart);
        check("mnuExportVideo", mnuExportVideo);
        check("butExportVideo", butExportVideo);

        check("mnuStylesheet", mnuStylesheet);

        check("helpBrowser", helpBrowser);

        helpBrowser.getEngine().load("about:blank");


        menuTabBindingSourceCode = new MenuTabBinding(projectTabPane, tabSourceCode, mnuShowSourceCode, FILE_ALIAS_SOURCE_CODE, true);
        menuTabBindingHelp = new MenuTabBinding(projectTabPane, tabHelp, mnuShowHelp, FILE_ALIAS_HELP, false);
        projectTabPane.getTabs().addListener((ListChangeListener<Tab>)(change -> {
            showFilePane(!projectTabPane.getTabs().isEmpty(), project);
        }));

        // TODO - implement Help and remove the following line
        mnuShowHelp.setVisible(false);

        BooleanBinding projectClosedBinding = projectPathProperty.isNull();
        mnuReloadProject.disableProperty().bind(projectClosedBinding);
        butReloadProject.disableProperty().bind(projectClosedBinding);
        mnuCloseProject.disableProperty().bind(projectClosedBinding);
        mnuEditProject.disableProperty().bind(projectClosedBinding);

        BooleanBinding tabClosedBinding = Bindings.createBooleanBinding(
                () -> projectTabPane.getSelectionModel().isEmpty(),
                projectTabPane.getSelectionModel().selectedItemProperty());
        mnuCloseTab.disableProperty().bind(tabClosedBinding);

        BooleanBinding chartExportDisabledBinding = viewsTabPane.getSelectionModel().selectedItemProperty().isNull();
        mnuExportChart.disableProperty().bind(chartExportDisabledBinding);
        butExportChart.disableProperty().bind(chartExportDisabledBinding);

        BooleanBinding videoExportDisabledBinding = Bindings.createBooleanBinding(() -> {
                ViewPane<?> viewPane = getCurrentViewPane();
                if(viewPane == null) return true;
                Parameter<?> parameter = viewPane.getIteratedParameter();
                if(parameter == null) return true;
                if(!parameter.isIterable()) return true;
                if(parameter.getPossibleValues() == null || parameter.getPossibleValues().isEmpty()) return true;
                return false;
            }, viewsTabPane.getSelectionModel().selectedItemProperty());

        mnuExportVideo.disableProperty().bind(videoExportDisabledBinding);
        butExportVideo.disableProperty().bind(videoExportDisabledBinding);

        sourceCodeArea.setClassLoaderManager(this);

        setProject(null, null, false);
    }


    public Stage getStage() {
        return (Stage)getScene().getWindow();
    }

    private static List<String> getOpenFiles(ProjectSettings projectSettings) {
        if(projectSettings == null) return new ArrayList<>();
        List<String> openFiles = projectSettings.getProperty(PROP_OPEN_FILE_TABS, new ArrayList<>(Arrays.asList(FILE_ALIAS_SOURCE_CODE)), false);
        projectSettings.setProperty(PROP_OPEN_FILE_TABS, (Serializable)openFiles);
        return openFiles;
    }

    private ChangeListener<Number> selectedIndexListener = null;
    private ChangeListener<Tab> selectedItemListener = null;
    private void updateProjectTabs(ProjectSettings projectSettings) {

        if(selectedIndexListener != null) {
            projectTabPane.getSelectionModel().selectedIndexProperty().removeListener(selectedIndexListener);
        }

        menuTabBindingSourceCode.setSettings(projectSettings, true);
        menuTabBindingHelp.setSettings(projectSettings, false);

        if(projectSettings != null) {
            selectedIndexListener = (obs, oldValue, newValue) -> projectSettings.setProperty(PROP_SELECTED_TAB_INDEX, newValue);
            projectTabPane.getSelectionModel().selectedIndexProperty().addListener(selectedIndexListener);

            selectedItemListener = (obs, oldTab, newTab) -> {
                butSaveFile.disableProperty().unbind();
                mnuSaveFile.disableProperty().unbind();
                butSaveFile.setDisable(true);
                mnuSaveFile.setDisable(true);
                if(newTab != null) {
                    CodeTabData tabData = getData(newTab);
                    Platform.runLater(() -> {
                        BooleanBinding scriptChangedBinding = Bindings.createBooleanBinding(
                                () -> !tabData.isDirty(), tabData.getTextProperty(), tabData.getInitialTextProperty());
                        butSaveFile.disableProperty().bind(scriptChangedBinding);
                        mnuSaveFile.disableProperty().bind(scriptChangedBinding);

                        StringBinding tabTextBinding = Bindings.createStringBinding(
                                () -> {
                                    String text = tabData.getTabText();
                                    if(tabData.isDirty()) {
                                        text = "*" + text;
                                    }
                                    return text;
                                },
                                tabData.getTextProperty(), tabData.getInitialTextProperty());
                        newTab.textProperty().bind(tabTextBinding);
                    });
                }

                Platform.runLater(() ->{
                    menuTabBindingSourceCode.setSettings(projectSettings, true);
                    menuTabBindingHelp.setSettings(projectSettings, false);

                    butSaveAll.disableProperty().unbind();
                    mnuSaveAll.disableProperty().unbind();
                    butSaveAll.setDisable(true);
                    mnuSaveAll.setDisable(true);

                    List<BooleanBinding> saveBindings = new ArrayList<>();
                    projectTabPane.getTabs().forEach(tab -> {
                        CodeTabData tabData = getData(tab);
                        saveBindings.add(Bindings.createBooleanBinding(() -> !tabData.isDirty(), tabData.getTextProperty(), tabData.getInitialTextProperty()));
                    });
                    if(!saveBindings.isEmpty()) {
                        BooleanBinding binding = saveBindings.get(0);
                        for(int i=1; i<saveBindings.size(); i++) {
                            binding = Bindings.and(binding, saveBindings.get(i));
                        }
                        mnuSaveAll.disableProperty().bind(binding);
                        butSaveAll.disableProperty().bind(binding);
                    }
                });
            };
            projectTabPane.getSelectionModel().selectedItemProperty().addListener(selectedItemListener);
        }

        int selectedTabIndex = (projectSettings == null) ? 0 : projectSettings.getProperty(PROP_SELECTED_TAB_INDEX, 0, false);

        ObservableList<Tab> tabs = projectTabPane.getTabs();
        tabs.clear();

        Tab selectedTab = null;
        List<String> openFiles = getOpenFiles(projectSettings);
        for(int i=0; i<openFiles.size(); i++) {
            String filePath = openFiles.get(i);
            Tab addedTab = null;
            if(filePath.equals(FILE_ALIAS_SOURCE_CODE)) {
                addedTab = tabSourceCode;
                String projectPath = projectPathProperty.get();
                String tabText = (projectPath == null) ? "Code" : new File(projectPath).getName();
                tabSourceCode.setUserData(new CodeTabData(tabText));
            } else if(filePath.equals(FILE_ALIAS_HELP)) {
                addedTab = tabHelp;
            } else {
                try {
                    File file = new File(filePath);
                    addedTab = CodeAreaTab.fromFile(file);
                    final Tab tab = addedTab;
                    tab.setOnCloseRequest(ev -> {if(!tryClose(tab)) ev.consume();});
                } catch(Exception e) {
                    addedTab = null;
                }
            }
            if(addedTab != null) {
                tabs.add(addedTab);
                getData(addedTab).setFilePath(filePath);
                if(i == selectedTabIndex) {
                    selectedTab = addedTab;
                }
            }
        }
        if(!tabs.isEmpty()) {
            if(selectedTab == null) {
                selectedTab = tabs.get(0);
            }
            projectTabPane.getSelectionModel().select(null);
            projectTabPane.getSelectionModel().select(selectedTab);
        }
    }

    private boolean tryClose(Tab tab) {
        if(project == null) return false;
        CodeTabData tabData = getData(tab);
        if(tabData.isDirty()) {
            ButtonType answer = Dialogs.confirmYesNoCancel("Close File", "This file has unsaved changes", "Save changes before closing the file?");
            if(answer == ButtonType.YES) {
                saveFile();
            } else if(answer == ButtonType.CANCEL) {
                return false;
            }
        }
        String filePath = tabData.getFilePath();
        ProjectSettings projectSettings = project.getProjectSettingsManager().getSettings();
        List<String> openFiles = getOpenFiles(projectSettings);
        openFiles.remove(filePath);
        return true;
    }


    private void updateRecentProjectPaths(String projectPath) {
        if(projectPath != null && !projectPath.isEmpty()) {
            GuiSettings guiSettings = guiSettingsManager.getSettings();
            int maxRecentProjects = guiSettings.getMaxRecentProjects();
            List<String> recentProjectPaths = guiSettings.getRecentProjectPaths();
            int pos = recentProjectPaths.indexOf(projectPath);
            if(pos < 0) {
                recentProjectPaths.add(0, projectPath);
            } else if(pos > 0) {
                for(int i=pos; i > 0; i--) {
                    recentProjectPaths.set(i, recentProjectPaths.get(i - 1));
                }
                recentProjectPaths.set(0, projectPath);
            }
            if(recentProjectPaths.size() > maxRecentProjects) {
                recentProjectPaths.subList(maxRecentProjects, recentProjectPaths.size()).clear();
            }
        }
    }

    private ChangeListener<Number> sourceCodeCaretPositionListener = null;
    public void setProject(FxProject newProject, String newProjectPath, boolean doOnlyViewUpdates) {
        if(newProject != null) {
            ProjectSettings projectSettings = newProject.getProjectSettingsManager().getSettings();
            String chartStylesheet = projectSettings.getProperty(PROP_CHART_STYLESHEET, null, true);
            if(chartStylesheet == null) {
                try {
                    chartStylesheet = new File(newProject.getName() + ".css").toURI().normalize().toURL().toExternalForm();
                    projectSettings.setProperty(PROP_CHART_STYLESHEET, chartStylesheet);
                } catch(Exception e) {
                    log.warn("Cannot get chartStylesheet uri.", e);
                }
            }
        }

        if(doOnlyViewUpdates) {
            List<String> recentProjectPaths = guiSettingsManager.getSettings().getRecentProjectPaths();
            String lastProjectPath = recentProjectPaths.isEmpty() ? null : recentProjectPaths.get(0);
            if(!Objects.equals(lastProjectPath, newProjectPath)) {
                log.warn("Changing project from '{}' to '{}'. doOnlyViewUpdates will be set to false.", lastProjectPath, newProjectPath);
                doOnlyViewUpdates = false;
            }
        }

        if(doOnlyViewUpdates) {
            forceCloseProject(false);
        } else {
            if(!tryCloseProject()) return;

            butSaveFile.disableProperty().unbind();
            butSaveFile.setDisable(newProject == null);

            mnuSaveFile.disableProperty().unbind();
            mnuSaveFile.setDisable(newProject == null);

            mnuShowSourceCode.disableProperty().unbind();
            mnuShowSourceCode.setDisable(newProject == null);

            if(sourceCodeCaretPositionListener != null) {
                sourceCodeArea.caretPositionProperty().removeListener(sourceCodeCaretPositionListener);
            }
        }

        this.project = newProject;
        this.projectPathProperty.set(newProjectPath);
        sourceCodeArea.setProjectModel(newProject);

        if(newProject == null) {
            updateProjectTabs(null);
            viewsTabPane.getTabs().clear();
            return;
        }

        updateRecentProjectPaths(newProjectPath);

        if(!doOnlyViewUpdates) {
            ProjectSettings projectSettings = newProject.getProjectSettingsManager().getSettings();
            updateProjectTabs(projectSettings);

            boolean showFilePane = projectSettings.getProperty(PROP_FILE_AREA_VISIBLE, true, false);
            showFilePane(showFilePane, newProject);

            Divider fileSplitDivider = fileSplitPane.getDividers().get(0);
            double fileSplitPosition = projectSettings.getProperty(PROP_FILE_SPLIT_PANE_DIVIDER_POSITION, -1.0, false);
            if(fileSplitPosition >= 0) {
                fileSplitDivider.setPosition(fileSplitPosition);
            }
            fileSplitDivider.positionProperty().addListener((observable, oldValue, newValue) ->
                    projectSettings.setProperty(PROP_FILE_SPLIT_PANE_DIVIDER_POSITION, newValue.doubleValue()));
            Platform.runLater(() -> {
                if(fileSplitPosition >= 0) {
                    fileSplitDivider.setPosition(fileSplitPosition);
                }
            });


            String projTitle = newProject.getTitle();
            getStage().setTitle(newProject.getName() + ((projTitle == null || projTitle.isEmpty()) ? "" : (" - " + projTitle)));

            sourceCodeArea.getCodeTemplates().clear();
            for(ViewrekaBundle bundle : project.getBundles()) {
                bundle.addTo(this);
                sourceCodeArea.getCodeTemplates().addAll(bundle.getTemplates());
            }

            String initialScriptText = newProject.getScriptText();
            sourceCodeArea.setText(initialScriptText);
            CodeTabData sourceData = getData(tabSourceCode);
            sourceData.setFilePath(FILE_ALIAS_SOURCE_CODE);
            sourceData.setInitialText(initialScriptText);
            sourceData.setTextProperty(sourceCodeArea.textProperty());

            // TODO - find a better way to position the caret.
            sourceCodeArea.positionCaret(0);
            Platform.runLater(() -> {
                int caretPos = projectSettings.getProperty(PROP_SOURCE_CODE_CARET_POSITION, 0, false);
                sourceCodeArea.selectRange(caretPos, caretPos);
                sourceCodeCaretPositionListener = (obs, oldValue, newValue) -> projectSettings.setProperty(PROP_SOURCE_CODE_CARET_POSITION, newValue);
                sourceCodeArea.caretPositionProperty().addListener(sourceCodeCaretPositionListener);
            });
        }

        refreshViews();

    }

    private void configureMainSplitDivider(FxProject newProject) {
        if(newProject == null) return;
        if(!mainSplitPane.getDividers().isEmpty()) {
            Divider mainSplitDivider = mainSplitPane.getDividers().get(0);
            ProjectSettings projectSettings = newProject.getProjectSettingsManager().getSettings();
            double mainSplitPosition = projectSettings.getProperty(PROP_MAIN_SPLIT_PANE_DIVIDER_POSITION, 0.3, false);
            if(mainSplitPosition >= 0) {
                mainSplitDivider.setPosition(mainSplitPosition);
            }
            mainSplitDivider.positionProperty().addListener((observable, oldValue, newValue) ->
                    projectSettings.setProperty(PROP_MAIN_SPLIT_PANE_DIVIDER_POSITION, newValue.doubleValue()));
            Platform.runLater(() -> {
                if(mainSplitPosition >= 0) {
                    mainSplitDivider.setPosition(mainSplitPosition);
                }
            });
        }

    }

    public void refreshViews() {
        if(project == null) return;

        project.getProjectSettingsManager().saveSettings();

        viewsTabPane.getTabs().clear();

        ProjectSettings projectSettings = project.getProjectSettingsManager().getSettings();
        String currentViewName = projectSettings.getCurrentView();

        Tab selectedTab = null;
        for(FxView view : project.getViews()) {
            String viewName = view.getName();
            final Tab tab = new Tab(viewName);
            viewsTabPane.getTabs().add(tab);
            if(viewName.equals(currentViewName)) {
                selectedTab = tab;
            }

            final ViewPane<?> viewPane = ViewPane.fromModel(view, projectSettings);
            tab.setContent(viewPane);

            viewPane.autosize();

            tab.setOnSelectionChanged(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    viewPane.autosize();
                    Tab selTab = viewsTabPane.getSelectionModel().getSelectedItem();
                    if(selTab != null) {
                        String selectedView = selTab.getText();
                        projectSettings.setCurrentView(selectedView);
                    }
                }
            });
        }

        if(selectedTab != null) {
            viewsTabPane.getSelectionModel().select(selectedTab);
        }
        layout();
    }
    public void editProject() {
        String projectPath = projectPathProperty.get();
        if(projectPath == null) return;
        File prjFile = new File(projectPath);
        String prjName = (project != null) ? project.getName() : prjFile.getName();
        File prjDir = prjFile.getParentFile();
        EditProject dialog = new EditProject(guiSettingsManager);
        dialog.getProjectLibs().initExistingLibs(prjDir.getAbsolutePath());
        dialog.showDialog().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dialog.getProjectLibs().installLibs(prjName, prjDir);
                openProject(prjFile, false);
                reloadProject();
            }
        });
    }


    public void newProject() {
        NewProject newProject = new NewProject(guiSettingsManager);
        Optional<File> optPrjFile = newProject.showDialog();
        if(optPrjFile.isPresent()) {
            File projectFile = optPrjFile.get();
            if(projectFile != null && projectFile.isFile()) {
                openProject(projectFile, false);
                initializeNewProject();
            }
        }
    }

    protected void initializeNewProject() {
        if(project != null && StringUtils.isBlank(sourceCodeArea.getText())) {
            setProjectClassLoader();
            try {
                project.getBundles().stream().filter(bundle -> bundle.getCategories().contains("datasource")).forEach(bundle -> {
                    Optional<CodeTemplate> template = bundle.getTemplates().stream().filter(tmpl -> "datasource".equals(tmpl.getKeyword())).findAny();
                    if(template.isPresent()) {
                        sourceCodeArea.insertCodeFragment(template.get());
                    }
                });
            } finally {
                resetClassLoader();
            }
        }
    }

    /** @return true, if current tab content has been successfully saved */
    public boolean saveFile() {
        Tab tab = projectTabPane.getSelectionModel().getSelectedItem();
        try {
            boolean saved = saveTabCode(tab);
            if(saved) openProject(true, true);
            return true;
        } catch(IOException e) {
            Dialogs.error("Save failed", "Cannot save the file", e);
            return false;
        }
    }

    /** @return true, if all modified files have been saved without errors. */
    public boolean saveAll() {
        int[] counters = {0};
        projectTabPane.getTabs().forEach(tab -> {
            try {
                saveTabCode(tab);
            } catch(Exception e) {
                Dialogs.error("Save failed", "Cannot save the text of " + getData(tab).getFilePath(), e);
                counters[0]++;
            }
        });
        int errors = counters[0];
        return (errors == 0);
    }


    /**
     * @param tab the tab whose code should be saved
     * @return true, if the code had modifications and it has been successfully saved.
     * @throws IOException
     */
    private boolean saveTabCode(Tab tab) throws IOException {
        boolean saved = false;
        CodeTabData data = getData(tab);
        String text = null;
        if(tab == tabSourceCode) {
            if(project != null) {
                text = sourceCodeArea.getText();
                String initialScriptText = project.getScriptText();
                if(!text.equals(initialScriptText)) {
                    project.saveScript(text);
                    saved = true;
                }
            }
        } else {
            String filePath = data.getFilePath();
            text = data.getTextProperty().getValue();
            if(filePath != null && text != null && !text.equals(data.getInitialText())) {
                try(Writer writer = new FileWriter(filePath)) {
                    writer.write(text);
                    saved = true;
                }
            }
        }
        if(saved) {
            data.setInitialText(text);
        }
        return saved;
    }

    public void toggleFilePane() {
        showFilePane(!mainSplitPane.getItems().contains(fileSplitPane), project);
    }

    public void showFilePane(boolean show, FxProject newProject) {
        ObservableList<Node> items = mainSplitPane.getItems();
        if(show) {
            if(!items.contains(fileSplitPane)) {
                items.add(0, fileSplitPane);
            }
            configureMainSplitDivider(project);
        } else {
            items.remove(fileSplitPane);
        }
        mnuToggleFilePane.setText((show ? "Hide" : "Show") + " File Area");
        if(newProject != null) {
            newProject.getProjectSettingsManager().getSettings().setProperty(PROP_FILE_AREA_VISIBLE, show);
        }
    }

    public void reloadProject() {
        String projectPath = projectPathProperty.get();
        if(projectPath == null) return;

        openProject(new File(projectPath), true);
    }

    public void openProject() {
        openProject(false, false);
    }

    public void openProject(boolean loadLastProject, boolean doOnlyViewUpdates) {
        GuiSettings guiSettings = guiSettingsManager.getSettings();

        File initialProjectDir = guiSettings.getMostRecentProjectDir();
        File projectFile = null;
        if(loadLastProject) {
            File initialProject = null;
            List<String> recentProjectPaths = guiSettings.getRecentProjectPaths();
            if(!recentProjectPaths.isEmpty() && (initialProjectDir != null) && initialProjectDir.isDirectory()) {
                try {
                    initialProject = new File(recentProjectPaths.get(0));
                    if(!initialProject.isFile()) {
                        initialProject = null;
                    }
                } catch(Exception e) {
                    log.debug("Error creating project file", e);
                    initialProject = null;
                }
            }
            projectFile = initialProject;
        } else {
            FileChooser projectChooser = new FileChooser();
            projectChooser.setTitle("Open Viewreka Project");
            projectChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Viewreka files", "*.viewreka"),
                    new FileChooser.ExtensionFilter("All files", "*.*")
                );
            projectChooser.setInitialDirectory(initialProjectDir);
            projectFile = projectChooser.showOpenDialog(getStage());
        }

        if(projectFile != null) {
            openProject(projectFile, doOnlyViewUpdates);
        }
    }

    public void onShown() {
        projectTabPane.requestFocus();
        Tab tab = projectTabPane.getSelectionModel().getSelectedItem();
        if(tab != null) {
            Platform.runLater(() -> tab.getContent().requestFocus());
        }
    }

    public void openProject(File projectFile, boolean doOnlyViewUpdates) {
        if(project != null) {
            project.getProjectSettingsManager().saveSettings();
        }
        FxProject tmpProject = null;
        try {
            if(!doOnlyViewUpdates && !tryCloseProject()) return;
            String projectUri = projectFile.toURI().normalize().toString();
            File parentDir = projectFile.getParentFile();
            if(parentDir != null) System.setProperty("user.dir", parentDir.getAbsolutePath());
            tmpProject = projectReader.getProject(projectUri);
            setProject(tmpProject, projectFile.getAbsolutePath(), doOnlyViewUpdates);
        } catch (Exception e) {
            if(tmpProject != null) {
                tmpProject.getScriptIssues().add(ScriptIssueImpl.fromThrowable(e));
            } else {
                forceCloseProject(true);
                showError("Open Project", "Unable to open project " + projectFile.getAbsolutePath(), e);
            }
        } finally {
            errorArea.clear();
            if(project != null) {
                project.getScriptIssues().forEach(issue -> errorArea.appendText(issue + "\n"));
            }
            errorArea.selectRange(0, 0);
        }
    }

    private boolean hasUnsavedChanges() {
        return projectTabPane.getTabs().stream().anyMatch(tab -> getData(tab).isDirty());
    }

    public boolean tryCloseProject() {
        if(hasUnsavedChanges()) {
            ButtonType answer = Dialogs.confirmYesNoCancel("Close Project", "One or more files have unsaved changes", "Save changes before closing the project?");
            if(answer == ButtonType.YES) {
                saveAll();
            } else if(answer == ButtonType.CANCEL) {
                return false;
            }
        }
        forceCloseProject(true);
        return true;
    }

    public void forceCloseProject(boolean clearGuiAndSaveSetttings) {
        if(clearGuiAndSaveSetttings) {
            if(project != null) {
                project.getProjectSettingsManager().saveSettings();
            }
            errorArea.clear();
            projectTabPane.getTabs().clear();
            viewsTabPane.getTabs().clear();
            sourceCodeArea.getCodeTemplates().clear();
        }

        if(project != null) {
            project.getDataSources().values().forEach(datasource -> {
                try {
                    datasource.close();
                } catch(Exception e) {
                    log.warn("Error closing datasource " + datasource.getName(), e);
                }
            });

            for(ViewrekaBundle bundle : project.getBundles()) {
                bundle.removeFrom(this);
            }
        }

        resetClassLoader();

        project = null;
        projectPathProperty.set(null);
    }

    public void newFile() {
        NewFile newFile = NewFile.createWith(guiSettingsManager);
        newFile.showDialog("New File");

        File file = newFile.getCreatedFile();
        if(file != null && file.isFile()) {
            openFile(file);
        }
    }

    public void openFile() {
        if(project != null) {
            ProjectSettings projectSettings = project.getProjectSettingsManager().getSettings();
            if(getOpenFiles(projectSettings).size() >= MAX_OPEN_FILES) {
                Dialogs.error("Open File error", "Too many open files.", "Please close some of your open files.", null);
                return;
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*"),
                new FileChooser.ExtensionFilter("Viewreka files", "*.viewreka"),
                new FileChooser.ExtensionFilter("SQL files", "*.sql"),
                new FileChooser.ExtensionFilter("CSS files", "*.css")
            );
        GuiSettings guiSettings = guiSettingsManager.getSettings();
        fileChooser.setInitialDirectory(guiSettings.getMostRecentProjectDir());
        File file = fileChooser.showOpenDialog(getScene().getWindow());
        openFile(file);
    }

    public void openFile(File file) {
        if(file != null && file.isFile()) {
            String filePath = file.getAbsolutePath();
            ObservableList<Tab> tabs = projectTabPane.getTabs();
            if(project != null) {
                if(file.toURI().normalize().equals(project.getScriptUri().normalize())) {
                    menuTabBindingSourceCode.showTab();
                    return;
                }
                ProjectSettings projectSettings = project.getProjectSettingsManager().getSettings();
                List<String> openFiles = getOpenFiles(projectSettings);
                int index = openFiles.indexOf(filePath);
                if(index >= 0) {
                    if(index >= tabs.size()) {
                        Dialogs.error("Open File error", "Internal error: invalid index (" + index + ") for already open file.");
                    } else {
                        Tab tab = tabs.get(index);
                        if(!filePath.equals(getData(tab).getFilePath())) {
                            Dialogs.error("Open File error", "Internal error: already open file not found at the expected index " + index + ".");
                        } else {
                            projectTabPane.getSelectionModel().select(index);
                        }
                    }
                    return;
                }
            }
            if(file.length() > MAX_TEXT_FILE_LENGTH) {
                Dialogs.error("Open File error", "File too big (" + file.length() + " bytes).");
                return;
            }
            if(isProbablyBinary(file)) {
                if(!Dialogs.confirmYesNo("Open File", "This seems to be a binary file.", "Are you sure you want to open this file?")) {
                    return;
                }
            }
            try {
                CodeAreaTab codeAreaTab = CodeAreaTab.fromFile(file);
                tabs.add(codeAreaTab);
                projectTabPane.getSelectionModel().select(codeAreaTab);

                if(project != null) {
                    ProjectSettings projectSettings = project.getProjectSettingsManager().getSettings();
                    List<String> openFiles = getOpenFiles(projectSettings);
                    openFiles.add(filePath);
                    codeAreaTab.setOnCloseRequest(ev -> {if(!tryClose(codeAreaTab)) ev.consume();});
                }
            } catch(IOException e) {
                Dialogs.error("Open File error", "Cannot open file '" + filePath + "'", e);
                return;
            }
        }
    }

    private static final Pattern CONTROL_PATTERN = Pattern.compile(".*[\\x00-\\x08].*");
    private static boolean isProbablyBinary(File file) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            if(!lines.isEmpty()) {
                long binCharCount = 0;
                long binLineCount =  0;
                for(String line : lines) {
                    if(CONTROL_PATTERN.matcher(line).matches()) return true;
                    if(line.chars().filter(ch -> !Character.isDefined(ch)).findFirst().isPresent()) return true;
                    long count = line.chars().filter(ch -> (ch > 127)).count();
                    if(count > 0) {
                        binCharCount += count;
                        binLineCount++;
                    }
                }
                if(binCharCount == 0 || binLineCount == 0) return false;
                long length = file.length();
                if(length < 100) {
                    if(binCharCount > 10) return true;
                } else {
                    if(100.0 * binCharCount / length > 10) return true;
                    if(100.0 * binLineCount / lines.size() > 50) return true;
                }
            }
        } catch(Throwable t) {
            log.error("An error occurred while analyzing the file", t);
            return true;
        }
        return false;
    }

    public void closeTab() {
        Tab tab = projectTabPane.getSelectionModel().getSelectedItem();
        if(tab != null) {
            EventHandler<Event> handler = tab.getOnClosed();
            if (handler != null) {
                handler.handle(null);
            }
            projectTabPane.getTabs().remove(tab);
        }
    }

    public void selectStylesheet() {
        if(project == null) return;

        ProjectSettings projectSettings = project.getProjectSettingsManager().getSettings();

        GuiSettings guiSettings = guiSettingsManager.getSettings();
        File initialCssDir = guiSettings.getMostRecentProjectDir();
        String chartStylesheet = projectSettings.getProperty(PROP_CHART_STYLESHEET, null, true);
        if(chartStylesheet != null) {
            try {
                URL cssUrl = new URI(chartStylesheet).toURL();
                if("file".equals(cssUrl.getProtocol())) {
                    File cssDir = new File(cssUrl.getFile()).getParentFile();
                    if(cssDir != null && cssDir.isDirectory()) {
                        initialCssDir = cssDir;
                    }
                }
            } catch(Exception e) {
                log.warn("Unable to retrieve directory of '" + chartStylesheet + "'", e);
            }
        }

        FileChooser cssChooser = new FileChooser();
        cssChooser.setTitle("Select stylesheet");
        cssChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSS files", "*.css"),
            new FileChooser.ExtensionFilter("All files", "*.*")
        );
        cssChooser.setInitialDirectory(initialCssDir);
        File cssFile = cssChooser.showOpenDialog(getStage());
        if(cssFile != null && cssFile.isFile()) {
            try {
                String newChartStylesheet = cssFile.toURI().normalize().toURL().toExternalForm();
                projectSettings.setProperty(PROP_CHART_STYLESHEET, newChartStylesheet);
            } catch(Exception e) {
                Dialogs.error("Stylesheet error", "Cannot set the stylesheet '" + cssFile + "'.", e);
            }
        }
        project.getProjectSettingsManager().saveSettings();
        refreshViews();
    }

    public void exportChart() {
        ViewPane<?> viewPane = getCurrentViewPane();
        if(viewPane == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export chart");

        List<String> acceptedFormats = Arrays.asList("png", "gif");
        ObservableList<ExtensionFilter> extensionFilters = fileChooser.getExtensionFilters();
        acceptedFormats.forEach(ext -> extensionFilters.add(new FileChooser.ExtensionFilter("PNG files", "*." + ext)));
        GuiSettings guiSettings = guiSettingsManager.getSettings();
        String initialDirPath = guiSettings.getProperty(PROP_CHART_EXPORT_DIR, guiSettings.getMostRecentProjectDir().getAbsolutePath(), false);
        File initialDir = new File(initialDirPath);

        fileChooser.setInitialDirectory(initialDir);
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if(file == null) return;

        if(file.getParentFile() != null) {
            guiSettings.setProperty(PROP_CHART_EXPORT_DIR, file.getParent());
        }

        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase();
        if(!acceptedFormats.contains(extension)) {
            extension = acceptedFormats.get(0);
        }

        WritableImage chartImage = viewPane.getChartImage();

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(chartImage, null), extension, file);
        } catch(IOException e) {
            Dialogs.error("Chart export failed", "Cannot export the chart image", e);
        }
    }

    private ViewPane<?> getCurrentViewPane() {
        Tab tab = viewsTabPane.getSelectionModel().getSelectedItem();
        if(tab == null) return null;
        Node content = tab.getContent();
        return (content instanceof ViewPane<?>) ? (ViewPane<?>) content : null;
    }

    private FxView getCurrentView() {
        if(project == null) return null;
        ProjectSettings projectSettings = project.getProjectSettingsManager().getSettings();
        String currentViewName = projectSettings.getCurrentView();
        if(currentViewName == null || currentViewName.isEmpty()) return null;
        FxView view = project.getViews().stream().filter(v -> currentViewName.equals(v.getName())).findFirst().orElse(null);
        return view;
    }

    public void exportVideo() {
        FxView view = getCurrentView();
        if(view == null) return;

        ViewPane<?> viewPane = getCurrentViewPane();
        if(viewPane == null) return;
        FxChartBuilder<?> chartBuilder = viewPane.getChartBuilder();
        if(chartBuilder == null) return;

        String iteratedParameterName = view.getViewSettings().getSelectedIteratedParameter();
        if(iteratedParameterName == null) return;
        Parameter<?> iteratedParameter = view.getParameters().get(iteratedParameterName);

        ExportVideo exportVideo = ExportVideo.createWith(
                guiSettingsManager,
                chartBuilder,
                iteratedParameter,
                viewPane.getChartFrameDurationMillis());
        exportVideo.showDialog("Export video");
    }

    @Override
    public void resetClassLoader() {
        Thread.currentThread().setContextClassLoader(originalClassLoader);
    }

    @Override
    public void setProjectClassLoader() {
        if(project != null) {
            try {
                ViewrekaClassLoader cl = new ViewrekaClassLoader();
                cl.configureScript(new GroovyCodeSource(project.getScriptUri()));
                Thread.currentThread().setContextClassLoader(cl);
                log.debug("Class loader for {}: {}", Thread.currentThread(), cl);
            } catch (IOException e) {
                project.getScriptIssues().add(ScriptIssueImpl.fromThrowable(e));
            }
        }
    }

    public void exitProgram() {
        getStage().close();
    }

    public void about() {
        Dialogs.info("About", "Viewreka " + VersionInfo.get());
    }

    private static void showError(String title, String header, Throwable t) {
        String msg = t.getMessage();
        if(msg == null || msg.isEmpty()) msg = t.toString();
        msg = msg.split("[\\r\\n]+")[0];
        Dialogs.error(title, header, msg, t);
    }

}
