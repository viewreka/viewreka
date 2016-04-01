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

import static org.beryx.viewreka.fxapp.codearea.CodeTabData.getData;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;

import org.apache.commons.io.FilenameUtils;
import org.beryx.viewreka.fxcommons.FXMLNode;

/**
 * A {@link Tab} containing a {@link SimpleCodeArea}.
 */
public class CodeAreaTab extends Tab implements FXMLNode {
    private final File file;
    private final String initialFileText;

    @FXML private SimpleCodeArea codeArea;

    public static CodeAreaTab fromFile(File file) throws IOException {
        CodeAreaTab tab = new CodeAreaTab(file).load();
        return tab;
    }

    private CodeAreaTab(File file) throws IOException {
        this.file = file;
        this.initialFileText = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        check("codeArea", codeArea);

        CodeAreaConfig config = FileAssociations.INSTANCE.getConfig(FilenameUtils.getExtension(file.getName()));
        codeArea.applyConfiguration(config);

        this.setText(file.getName());
        CodeTabData data = getData(this);
        data.setFilePath(file.getAbsolutePath());
        data.setInitialText(initialFileText);
        data.setTextProperty(codeArea.textProperty());

        codeArea.setText(initialFileText);
    }
}
