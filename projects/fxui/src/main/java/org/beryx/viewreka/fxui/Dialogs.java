/**
 * Inspired by (and using code from) http://code.makery.ch/blog/javafx-dialogs-official/
 */
package org.beryx.viewreka.fxui;

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

public class Dialogs {

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

	public static void info(String title, String header, String text) {
		alert(AlertType.INFORMATION, title, header, text, null);
	}

	public static void info(String title, String text) {
		info(title, null, text);
	}

	public static void warning(String title, String header, String text) {
		alert(AlertType.WARNING, title, header, text, null);
	}

	public static void warning(String title, String text) {
		warning(title, null, text);
	}

	public static void error(String title, String header, String text, Throwable t) {
		alert(AlertType.ERROR, title, header, text, t);
	}

	public static void error(String title, String text, Throwable t) {
		alert(AlertType.ERROR, title, null, text, t);
	}

	public static void error(String title, String text) {
		error(title, null, text, null);
	}

	public static boolean confirm(String title, String header, String text) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(text);

		Optional<ButtonType> result = alert.showAndWait();
		return (result.get() == ButtonType.OK);
	}

	public static boolean confirmYesNo(String title, String header, String text) {
		ButtonType result = confirm(title, header, text, ButtonType.YES, ButtonType.NO).orElse(ButtonType.NO);
		return result == ButtonType.YES;
	}

	public static ButtonType confirmYesNoCancel(String title, String header, String text) {
		ButtonType result = confirm(title, header, text, ButtonType.YES, ButtonType.NO, ButtonType.CANCEL).orElse(ButtonType.CANCEL);
		return result;
	}

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
