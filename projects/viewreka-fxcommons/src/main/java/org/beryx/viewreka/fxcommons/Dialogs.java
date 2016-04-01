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


/**
 * Inspired by (and using code from) http://code.makery.ch/blog/javafx-dialogs-official/
 */
package org.beryx.viewreka.fxcommons;

import java.io.PrintWriter;
/**
 * Based on the examples provided at http://code.makery.ch/blog/javafx-dialogs-official/
 */
import java.io.StringWriter;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Static methods for displaying dialogs
 */
public class Dialogs {

    /**
     * Displays a dialog with the specified type and content
     * @param type the dialog type
     * @param title the dialog title
     * @param header the dialog header
     * @param text the dialog text
     * @param t if not null, the dialog will allow displaying the stack trace of this {@link Throwable}
     */
    public static void alert(AlertType type, String title, String header, String text, Throwable t) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        if(t != null) {
            // Create expandable Exception.
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = new Label("The exception stacktrace was:");

            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            textArea.setWrapText(true);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            // Set expandable Exception into the dialog pane.
            alert.getDialogPane().setExpandableContent(expContent);
        }

        alert.showAndWait();
    }

    /**
     * Displays an info dialog with the specified title, header and text
     */
    public static void info(String title, String header, String text) {
        alert(AlertType.INFORMATION, title, header, text, null);
    }

    /**
     * Displays an info dialog with the specified title and text
     */
    public static void info(String title, String text) {
        info(title, null, text);
    }

    /**
     * Displays a warning dialog with the specified title, header and text
     */
    public static void warning(String title, String header, String text) {
        alert(AlertType.WARNING, title, header, text, null);
    }

    /**
     * Displays a warning dialog with the specified title, header and text
     */
    public static void warning(String title, String text) {
        warning(title, null, text);
    }

    /**
     * Displays an error dialog with the specified title, header, text and throwable
     */
    public static void error(String title, String header, String text, Throwable t) {
        alert(AlertType.ERROR, title, header, text, t);
    }

    /**
     * Displays an error dialog with the specified title, text and throwable
     */
    public static void error(String title, String text, Throwable t) {
        alert(AlertType.ERROR, title, null, text, t);
    }

    /**
     * Displays an error dialog with the specified title and text
     */
    public static void error(String title, String text) {
        error(title, null, text, null);
    }

    /**
     * Displays an OK/Cancel confirmation dialog with the specified title, header and text
     */
    public static boolean confirm(String title, String header, String text) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        Optional<ButtonType> result = alert.showAndWait();
        return (result.get() == ButtonType.OK);
    }

    /**
     * Displays a Yes/No confirmation dialog with the specified title, header and text
     */
    public static boolean confirmYesNo(String title, String header, String text) {
        ButtonType result = confirm(title, header, text, ButtonType.YES, ButtonType.NO).orElse(ButtonType.NO);
        return result == ButtonType.YES;
    }

    /**
     * Displays a Yes/No/Cancel confirmation dialog with the specified title, header and text
     */
    public static ButtonType confirmYesNoCancel(String title, String header, String text) {
        ButtonType result = confirm(title, header, text, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL).orElse(ButtonType.CANCEL);
        return result;
    }

    /**
     * Displays a confirmation dialog with the specified title, header and text and with a specified combination of buttons
     * @return an {@link Optional} providing the chosen button or empty if no button has been pressed.
     */
    public static Optional<ButtonType> confirm(String title, String header, String text, ButtonType... buttons) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        alert.getButtonTypes().clear();
        for(ButtonType button : buttons) {
            alert.getButtonTypes().add(button);
        }
        Optional<ButtonType> result = alert.showAndWait();
        return result;
    }
}
