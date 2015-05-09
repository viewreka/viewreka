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

import org.beryx.viewreka.fxui.FxUtil;
import org.beryx.viewreka.parameter.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextFieldParameterEditor<T> extends FxParameterEditor<T> {
	private static final Logger log = LoggerFactory.getLogger(TextFieldParameterEditor.class);

	@FXML private Label lbPrmName;
	@FXML private TextField prmText;
	@FXML private Button butSaveValue;
	@FXML private Button butCancelValue;

	private String currentValue;

	public static class Builder<TT> implements FxParameterEditorBuilder<TT> {
		@Override
		public FxParameterEditor<TT> createEditor(Parameter<TT> parameter, Parent parentPane) {
			return new TextFieldParameterEditor<>(parameter);
		}
	}

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

	public void keyPressed(KeyEvent ev) {
		if(ev.getCode() == KeyCode.ENTER) {
			trySaveValue();
		}
	}

	public void trySaveValue() {
		if(!isValidChanged()) {
			log.warn("Invalid input.");
		} else {
			saveValue();
		}
	}

	public void saveValue() {
		currentValue = prmText.getText();
		parameter.setValueFromString(currentValue);
		updateEditor();
	}

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

	public boolean isChanged() {
		return !prmText.getText().equals(currentValue);
	}

	public boolean isValid() {
		try {
			parameter.toValidValue(prmText.getText());
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public boolean isValidChanged() {
		return isChanged() && isValid();
	}

	public void prmEdit() {
		updateEditor();
	}
}
