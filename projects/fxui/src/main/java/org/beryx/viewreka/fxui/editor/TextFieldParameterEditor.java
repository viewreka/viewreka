package org.beryx.viewreka.fxui.editor;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.beryx.viewreka.fxcommons.FxUtil;
import org.beryx.viewreka.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A parameter editor that allows changing the parameter value in a text field.
 * Two small buttons are displayed next to the text field, allowing to confirm the changes or to restore the previous value.
 * Pressing {@code ENTER} will also confirm the changes.
 * @param <T> the type of the parameter handled by this editor
 */
public class TextFieldParameterEditor<T> extends FxParameterEditor<T> {
    private static final Logger log = LoggerFactory.getLogger(TextFieldParameterEditor.class);

    @FXML private Label lbPrmName;
    @FXML private TextField prmText;
    @FXML private Button butSaveValue;
    @FXML private Button butCancelValue;

    private String currentValue;

    /**
     * The default builder of TextFieldParameterEditor.
     * @param <TT> the type of the {@link TextFieldParameterEditor}s created by this builder
     */
    public static class Builder<TT> implements FxParameterEditorBuilder<TT> {
        @Override
        public FxParameterEditor<TT> createEditor(Parameter<TT> parameter, Parent parentPane) {
            return new TextFieldParameterEditor<>(parameter);
        }
    }

    /**
     * @param parameter the parameter handled by this editor
     */
    public TextFieldParameterEditor(Parameter<T> parameter) {
        super(parameter);
    }

    @Override
    public void valueChanged(Parameter<T> prm, T oldValue) {
        throw new UnsupportedOperationException("A TextFieldParameterEditor cannot act as ParameterListener.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        check("lbPrmName", lbPrmName);
        check("prmText", prmText);
        check("butAcceptValue", butSaveValue);
        check("butCancelValue", butCancelValue);

//        getStylesheets().add(this.getClass().getResource("editor.css").toExternalForm());

        log.debug("Initializing TextFieldParameterEditor({})", parameter.getName());

        lbPrmName.setText(parameter.getName());

        currentValue = parameter.getValueAsString();
        prmText.setText(currentValue);

        prmText.textProperty().addListener((observable, oldValue, newValue) -> updateEditor());
    }

    /**
     * Called when a key has been pressed.
     * @param ev the associated key event
     */
    public void keyPressed(KeyEvent ev) {
        if(ev.getCode() == KeyCode.ENTER) {
            trySaveValue();
        }
    }

    /**
     * Checks the content of the text field and, if valid, use it to set the new parameter value.
     */
    public void trySaveValue() {
        if(!isValidChanged()) {
            log.warn("Invalid input.");
        } else {
            saveValue();
        }
    }

    /**
     * Sets the new parameter value based on the content of the text field and updates this editor.
     */
    public void saveValue() {
        currentValue = prmText.getText();
        parameter.setValueFromString(currentValue);
        updateEditor();
    }

    /**
     * Replaces the content of the text field with the last parameter value.
     */
    public void cancelValue() {
        prmText.setText(currentValue);
        updateEditor();
        prmText.requestFocus();
    }


    private static final List<String> SUFFIXES = Arrays.asList("changed-valid", "changed-invalid", "unchanged-valid", "unchanged-invalid");

    @Override
    public void updateEditor() {
        butCancelValue.setDisable(!isChanged());
        butSaveValue.setDisable(!isValidChanged());

        String suffix = (isChanged() ? "changed" : "unchanged") + "-" + (isValid() ? "valid" : "invalid");
        FxUtil.applyStyle("input", SUFFIXES, suffix, prmText);
    }

    /**
     * @return true, if the text field does not contain the last parameter value.
     */
    public boolean isChanged() {
        return !prmText.getText().equals(currentValue);
    }

    /**
     * @return true, if the content of the text field represents a valid value for the handled parameter
     */
    public boolean isValid() {
        try {
            parameter.toValidValue(prmText.getText());
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    /**
     * @return true, if the content of the text field represents a valid value and differs from the last value of the handled parameter
     */
    public boolean isValidChanged() {
        return isChanged() && isValid();
    }

    /**
     * Updates the parameter editor
     */
    public void prmEdit() {
        updateEditor();
    }
}
