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
