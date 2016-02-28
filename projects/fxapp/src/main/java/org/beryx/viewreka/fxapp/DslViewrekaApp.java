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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.beryx.vbundle.chart.axis.AxisBundle;
import org.beryx.vbundle.chart.html.HtmlChartBundle;
import org.beryx.vbundle.chart.xy.XYChartBundle;
import org.beryx.vbundle.derby.DerbyBundle;
import org.beryx.vbundle.parameter.ParameterBundle;
import org.beryx.vbundle.sql.SqlBundle;
import org.beryx.viewreka.bundle.api.ViewrekaBundle;
import org.beryx.viewreka.dsl.ViewBundle;
import org.beryx.viewreka.dsl.project.ProjectBuilder;
import org.beryx.viewreka.fxui.FxGui;
import org.beryx.viewreka.fxui.FxProject;
import org.beryx.viewreka.project.ProjectReader;
import org.beryx.viewreka.settings.ProjectSettings;
import org.beryx.viewreka.settings.SettingsManager;

/**
 * The main class of the Viewreka GUI.
 */
public class DslViewrekaApp extends ViewrekaApp {
    private final List<ViewrekaBundle<FxGui>> defaultBundles = Arrays.asList(
            new ViewBundle(),
            new ParameterBundle(),
            new SqlBundle(),
            new AxisBundle(),
            new HtmlChartBundle(),
            new XYChartBundle(),
            new DerbyBundle());
    @Override
    protected ProjectReader<FxProject> getProjectReader() {
        return new ProjectReader<FxProject>() {
            @Override
            public FxProject getProject(String projectUri) {
                FxProject project = ProjectBuilder.createFromUri(projectUri);
                project.getBundles().addAll(defaultBundles);
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
