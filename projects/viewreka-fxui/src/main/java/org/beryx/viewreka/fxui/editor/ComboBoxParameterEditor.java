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
package org.beryx.viewreka.fxui.editor;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import org.beryx.viewreka.fxcommons.Dialogs;
import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.parameter.Parameter.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A parameter editor that allows setting the desired parameter value by choosing one of the possible values provided by a combobox.
 * @param <T> the type of the parameter handled by this editor
 */
public class ComboBoxParameterEditor<T> extends FxParameterEditor<T> {
    private static final Logger log = LoggerFactory.getLogger(ComboBoxParameterEditor.class);

    @FXML private Label lbPrmName;
    @FXML private ComboBox<String> prmCombo;

    /**
     * The default builder of ComboBoxParameterEditor.
     * @param <TT> the type of the {@link ComboBoxParameterEditor}s created by this builder
     */
    public static class Builder<TT> implements FxParameterEditorBuilder<TT> {
        @Override
        public FxParameterEditor<TT> createEditor(Parameter<TT> parameter, Parent parentPane) {
            return new ComboBoxParameterEditor<>(parameter);
        }
    }


    /**
     * @param parameter the parameter handled by this editor
     */
    public ComboBoxParameterEditor(Parameter<T> parameter) {
        super(parameter);
    }

    @Override
    public void valueChanged(Parameter<T> prm, T oldValue) {
        Platform.runLater(() -> refreshCombo());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        check("lbPrmName", lbPrmName);
        check("prmCombo", prmCombo);

        log.debug("Initializing ComboBoxParameterEditor({})", parameter.getName());

        lbPrmName.setText(parameter.getName());

        try {
            refreshCombo();
        } catch(Exception e) {
            Dialogs.error("Initialization error", "Cannot retrieve the possible values of parameter " + parameter.getName(), e);
        }

        prmCombo.valueProperty().addListener((observable, oldValue, newValue) -> {if(newValue != null) saveValue();});
    }

    /**
     * Refresh the combobox items. This method is typically called when the list of possible values of the handled parameter has changed.
     */
    public void refreshCombo() {
        log.debug("Applying refreshCombo() to {}...", this);
        prmCombo.getItems().clear();
        String currentDisplayedValue = null;
        List<Value<T>> possibleValues = parameter.getPossibleValues();
        for(Value<T> value : possibleValues) {
            prmCombo.getItems().add(value.getDisplayedValue());
            if(Objects.equals(value.getValue(), parameter.getValue())) {
                currentDisplayedValue = value.getDisplayedValue();
            }
        }
        if(currentDisplayedValue == null && !possibleValues.isEmpty()) {
            currentDisplayedValue = possibleValues.get(0).getDisplayedValue();
        }
        prmCombo.setValue(currentDisplayedValue);

        log.debug("combo items for {}: {}, selected: {}", this, prmCombo.getItems(), currentDisplayedValue);
    }


    @Override
    public void updateEditor() {
        refreshCombo();
    }

    /**
     * Sets the parameter value corresponding to the selected combobox option.
     */
    public void saveValue() {
        String currentDisplayedValue = prmCombo.getValue();
        T currentValue = null;
        List<Value<T>> possibleValues = parameter.getPossibleValues();
        for(Value<T> value : possibleValues) {
            if(Objects.equals(value.getDisplayedValue(), currentDisplayedValue)) {
                currentValue = value.getValue();
            }
        }
        parameter.setValue(currentValue);
    }
}
