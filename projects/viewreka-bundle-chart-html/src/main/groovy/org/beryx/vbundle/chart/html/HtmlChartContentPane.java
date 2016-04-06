/**
 * Copyright 2016 the original author or authors.
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
package org.beryx.vbundle.chart.html;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.beryx.viewreka.fxcommons.FXMLNode;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * A pane providing controls for configuring the content of an HTML chart.
 * This pane will be embedded in the dialog displayed by the {@link org.beryx.viewreka.bundle.api.CodeTemplate} of  {@link HtmlChartBundle}.
 */
public class HtmlChartContentPane extends BorderPane implements FXMLNode {
    @FXML private RadioButton optInline;
    @FXML private RadioButton optFile;
    @FXML private RadioButton optUrl;

    @FXML private HBox hboxInline;
    @FXML private HBox hboxFile;
    @FXML private HBox hboxUrl;

    @FXML private TextField txtInline;
    @FXML private TextField txtFile;
    @FXML private TextField txtUrl;

    @FXML private Button butFile;

    public HtmlChartContentPane() {
        load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        check("optInline", optInline);
        check("optFile", optFile);
        check("optUrl", optUrl);
        check("hboxInline", hboxInline);
        check("hboxFile", hboxFile);
        check("hboxUrl", hboxUrl);
        check("txtInline", txtInline);
        check("txtFile", txtFile);
        check("txtUrl", txtUrl);
        check("butFile", butFile);

        hboxInline.visibleProperty().bind(optInline.selectedProperty());
        hboxFile.visibleProperty().bind(optFile.selectedProperty());
        hboxUrl.visibleProperty().bind(optUrl.selectedProperty());
    }

    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("HTML file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("HTML files", "*.html", "*.htm"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        String initialDirPath = System.getProperty("user.dir");
        File initialDir = new File(initialDirPath);
        fileChooser.setInitialDirectory(initialDir);

        File htmlFile = fileChooser.showOpenDialog(getScene().getWindow());
        if(htmlFile != null) {
            txtFile.setText(htmlFile.getAbsolutePath());
        }
    }

    public String getContent() {
        if(optFile.isSelected()) {
            String filePath = txtFile.getText().replace('\\', '/');
            Path path = Paths.get(filePath);
            Path pathUserDir = Paths.get(System.getProperty("user.dir").replace('\\', '/'));
            if(path.startsWith(pathUserDir)) {
                path = pathUserDir.relativize(path);
            }
            return "file('" + path.toString().replace('\\', '/') + "')";
        } else if(optUrl.isSelected()) {
            return "url('" + txtUrl.getText() + "')";
        } else {
            return "\"" + StringEscapeUtils.escapeJava(txtInline.getText()) + "\"";
        }
    }

    public void setInlineContent(String inlineContent) {
        txtInline.setText(inlineContent);
    }
}
