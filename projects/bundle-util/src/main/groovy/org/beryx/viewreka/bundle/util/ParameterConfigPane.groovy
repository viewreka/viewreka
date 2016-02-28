/*
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
package org.beryx.viewreka.bundle.util

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Stage
import org.beryx.viewreka.fxcommons.Dialogs

class ParameterConfigPane extends GridPane {
    final ParameterizedTemplate template
    final Map<String, String> values = [:]

    ParameterConfigPane(Stage stage, ParameterizedTemplate template) {
        this.template = template

        ColumnConstraints col1 = new ColumnConstraints(60, -1, Double.MAX_VALUE)
        col1.hgrow = Priority.SOMETIMES
        ColumnConstraints col2 = new ColumnConstraints(80, 240, Double.MAX_VALUE)
        col2.hgrow = Priority.ALWAYS
        columnConstraints.addAll(col1, col2)

        setAlignment(Pos.CENTER)
        setHgap(10)
        setVgap(10)
        setPadding(new Insets(25, 25, 25, 25))

        Text title = new Text(template.name)

        title.font = Font.font("Tahoma", FontWeight.NORMAL, 20)
        add(title, 0, 0, 2, 1)

        List<Control> controls = []
        for(int i=0; i < template.parameters.size(); i++) {
            TemplateParameter prm = template.parameters[i]
            Label lb = new Label(prm.name)
            lb.tooltip = new Tooltip(prm.description)
            add(lb, 0, i+1)

            Class<? extends Control> ctrlType = prm.controlType
            try {
                Control control = ctrlType.getConstructor().newInstance()
                control.id = "prm-$prm.name"
                prm.textSetter.accept(control, prm.sampleValue)
                control.tooltip = new Tooltip(prm.description)
                add(control, 1, i+1)
                controls.add(control)
            } catch (Exception e) {
                throw new RuntimeException("Cannot create input control for parameter " + prm, e)
            }
        }

        Button butOk = new Button("OK")
        butOk.id = "butOk"
        butOk.defaultButton = true
        butOk.onAction = {event ->
            if(readControlValues(controls, values) && checkValues(values)) {
                stage.close()
            } else {
                values.clear()
            }
        }

        Button butCancel = new Button("Cancel")
        butCancel.id = "butCancel"
        butCancel.onAction = {event -> stage.close()}

        HBox hbBtn = new HBox(10)
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT)
        hbBtn.children.add(butOk)
        hbBtn.children.add(butCancel)
        add(hbBtn, 1, template.parameters.size() + 2)
    }

    protected boolean readControlValues(List<Control> controls, Map<String, String> values) {
        for(int i=0; i < template.parameters.size(); i++) {
            TemplateParameter prm = template.parameters[i]
            Control control = controls[i]
            String val = prm.textGetter.apply(control)
            String errMsg = prm.getValidationErrorMessage(val)
            if(errMsg != null) {
                Dialogs.error("Invalid parameters", errMsg)
                return false
            }
            values.put(prm.getName(), val)
        }
        true
    }

    protected boolean checkValues(Map<String, String> values) {
        for(def validator : template.validators) {
            String errMsg = validator.apply(values)
            if(errMsg != null) {
                boolean keep = Dialogs.confirmYesNo("Invalid parameters", errMsg,
                        "Do you still want to use this configuration?")
                if(!keep) return false
            }
        }
        true
    }
}
