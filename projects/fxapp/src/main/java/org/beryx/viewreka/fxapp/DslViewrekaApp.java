package org.beryx.viewreka.fxapp;

import java.util.ArrayList;

import org.beryx.viewreka.dsl.project.ProjectBuilder;
import org.beryx.viewreka.fxui.FxProject;
import org.beryx.viewreka.project.ProjectReader;
import org.beryx.viewreka.settings.ProjectSettings;
import org.beryx.viewreka.settings.SettingsManager;

/**
 * The main class of the Viewreka GUI.
 */
public class DslViewrekaApp extends ViewrekaApp {
    @Override
    protected ProjectReader<FxProject> getProjectReader() {
        return new ProjectReader<FxProject>() {
            @Override
            public FxProject getProject(String projectUri) {
                FxProject project = ProjectBuilder.createFromUri(projectUri);
                SettingsManager<ProjectSettings> settingsManager = project.getProjectSettingsManager();
                settingsManager.addCreationListener(settings -> {
                    ArrayList<String> openFiles = new ArrayList<>();
                    openFiles.add(Viewreka.FILE_ALIAS_SOURCE_CODE);
                    settings.setProperty(Viewreka.PROP_OPEN_FILE_TABS, openFiles);
                });
                return project;
            }
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}
