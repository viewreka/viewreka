package org.beryx.viewreka.fxapp;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.beryx.viewreka.fxui.FxProject;
import org.beryx.viewreka.fxui.settings.GuiSettings;
import org.beryx.viewreka.fxui.settings.GuiSettingsManager;
import org.beryx.viewreka.project.ProjectReader;

/**
 * Base {@link Application} class for Viewreka GUIs. Concrete subclasses must provide a {@link ProjectReader} for retrieving the {@link FxProject}s handled by the GUI.
 */
public abstract class ViewrekaApp extends Application {

    /**
     * @return a {@link ProjectReader} for retrieving the {@link FxProject}s handled by the GUI.
     */
    protected abstract ProjectReader<FxProject> getProjectReader();

    @Override
    public void start(Stage stage) throws IOException {
        GuiSettingsManager guiSettingsManager = new GuiSettingsManager();
        Viewreka viewreka = new Viewreka(getProjectReader(), guiSettingsManager).load();
        Scene scene = new Scene(viewreka, viewreka.getPrefWidth(), viewreka.getPrefHeight());

        stage.setOnCloseRequest(event -> {
                if(!viewreka.tryCloseProject()) {
                    event.consume();
                    return;
                }
                guiSettingsManager.saveSettings();
        });

        stage.setScene(scene);

        GuiSettings settings = guiSettingsManager.getSettings();

        stage.setX(settings.getWindowX());
        stage.setY(settings.getWindowY());
        stage.setWidth(settings.getWindowWidth());
        stage.setHeight(settings.getWindowHeight());

        List<String> parameters = getParameters().getUnnamed();
        if(!parameters.isEmpty()) {
            String projectPath = parameters.get(0);
            File projectFile = new File(projectPath);
            viewreka.openProject(projectFile, false);
        } else if(settings.isLoadLastProject()) {
            viewreka.openProject(settings.isLoadLastProject(), false);
        }

        stage.xProperty().addListener((observable, oldValue, newValue) -> settings.setWindowX(newValue.doubleValue()));
        stage.yProperty().addListener((observable, oldValue, newValue) -> settings.setWindowY(newValue.doubleValue()));
        stage.widthProperty().addListener((observable, oldValue, newValue) -> settings.setWindowWidth(newValue.doubleValue()));
        stage.heightProperty().addListener((observable, oldValue, newValue) -> settings.setWindowHeight(newValue.doubleValue()));

        stage.show();
    }
}
