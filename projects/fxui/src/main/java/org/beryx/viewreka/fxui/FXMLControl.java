package org.beryx.viewreka.fxui;

import static org.beryx.viewreka.core.Util.requireNonNull;

import java.io.IOException;
import java.net.URL;

import javafx.event.EventTarget;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;

public interface FXMLControl extends Initializable {
	@SuppressWarnings("unchecked")
	default <T extends FXMLControl> T load() {
		String fxmlFileName = getClass().getSimpleName() + ".fxml";
		URL fxmlLocation = requireNonNull(getClass().getResource(fxmlFileName), "URL of resource '" + fxmlFileName + "'");
		FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
			Object loaded = fxmlLoader.load();
			if(loaded != this) throw new AssertionError();
		} catch (IOException e) {
			throw new AssertionError("Cannot load FXML resource " + fxmlFileName, e);
		}
        if(this instanceof Parent) {
        	String cssName = getClass().getSimpleName() + ".css";
        	URL cssUrl = getClass().getResource(cssName);
        	if(cssUrl != null) {
				((Parent)this).getStylesheets().add(cssUrl.toExternalForm());
        	}
        }
        return (T)this;
	}

	default void check(String name, EventTarget item) {
		if(item == null) {
			throw new AssertionError("fx:id=\"" + name + "\" was not injected: check your FXML file '" + getClass().getSimpleName() + ".fxml'.");
		}
	}
}
