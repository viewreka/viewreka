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
package org.beryx.viewreka.settings;

/**
 * A concrete settings manager implementation for handling project settings.
 */
public class ProjectSettingsManager extends SettingsManagerImpl<ProjectSettings> {

    /**
     * Constructs a ProjectSettingsManager that stores the settings in a specified file.
     * @param settingsDirPath the path to the directory of the file used to store the project settings
     * @param settingsFileName the name of the file used to store the project settings
     */
    public ProjectSettingsManager(String settingsDirPath, String settingsFileName) {
        super(settingsDirPath, settingsFileName);
    }

    @Override
    protected ProjectSettings createNewSettings() {
        return new ProjectSettingsImpl();
    }

}
