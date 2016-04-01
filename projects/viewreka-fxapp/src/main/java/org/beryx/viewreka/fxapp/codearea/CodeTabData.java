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
package org.beryx.viewreka.fxapp.codearea;

import java.util.Objects;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;

import org.beryx.viewreka.core.Util;

/**
 * A class containing information used as user data by a {@link CodeAreaTab}.
 */
public class CodeTabData {
    private final String tabText;
    private String filePath;
    private final StringProperty initialTextProperty = new SimpleStringProperty();
    private ObservableValue<String> textProperty = new SimpleStringProperty();

    public static CodeTabData getData(Tab tab) {
        Util.requireNonNull(tab, "CodeTabData.tab");
        Object userData = tab.getUserData();
        if(userData instanceof CodeTabData) {
            return (CodeTabData) userData;
        }
        String tabText = tab.getText();
        if(tabText.startsWith("*")) tabText = tabText.substring(1);
        CodeTabData data = new CodeTabData(tabText);
        tab.setUserData(data);
        return data;
    }

    public CodeTabData(String tabText) {
        this.tabText = tabText;
    }

    public String getTabText() {
        return tabText;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public StringProperty getInitialTextProperty() {
        return initialTextProperty;
    }
    public String getInitialText() {
        return initialTextProperty.get();
    }
    public void setInitialText(String text) {
        initialTextProperty.set(text);
    }

    public ObservableValue<String> getTextProperty() {
        return textProperty;
    }
    public void setTextProperty(ObservableValue<String> textProperty) {
        this.textProperty = textProperty;
    }

    public boolean isDirty() {
        String text = (textProperty == null) ? null : textProperty.getValue();
        String initialText = initialTextProperty.get();
        if(initialText != null) {
            // RichTextFx uses '\n' as line terminator
            initialText = initialText.replaceAll("\r\n|\r", "\n");
        }
        return !Objects.equals(initialText, text);
    }

    @Override
    public String toString() {
        return tabText;
    }
}
