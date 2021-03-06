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
package org.beryx.viewreka.fxcommons;

import java.io.IOException;
import java.net.URL;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.event.EventTarget;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * A utility interface typically implemented by user-defined {@link Node} subclasses.
 * This interface provides default methods implementing a "convention over configuration" approach.
 */
public interface FXMLNode extends Initializable {
    /**
     * Loads the object hierarchy of this FXML node using a "convention over configuration" approach.
     * The method expects to find a resource file with the same name as the simple name of this FXML node's class and with the {@code .fxml} extension.
     * An {@link FXMLLoader} will be created using the location of this file as constructor argument. Both the root and the controller of the FXMLLoader will be set to this FXML node.
     * After loading the object hierarchy via the FXMLLoader, the method checks whether this FXML node is an instance of {@link Parent}
     * and whether a CSS resource file with the same name as the simple name of this FXML node's class exists. If this is the case, the CSS file will be used for this FXML node.
     * @return this FXML node
     */
    @SuppressWarnings("unchecked")
    @SuppressFBWarnings("UI_INHERITANCE_UNSAFE_GETRESOURCE") // Not a bug: we really expect the resource to be present in the package in which the implementing class is defined.
    default <T extends FXMLNode> T load() {
        String fxmlFileName = getClass().getSimpleName() + ".fxml";
        URL fxmlLocation = getClass().getResource(fxmlFileName);
        if(fxmlLocation == null) throw new AssertionError("URL of resource '" + fxmlFileName + "'");
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

    /**
     * Checks whether a specified {@code @FXML}-annotated field has been injected by the {@link #load()} method.
     * @param name the name of the field to be checked
     * @param item the field to be checked
     */
    default void check(String name, EventTarget item) {
        if(item == null) {
            throw new AssertionError("fx:id=\"" + name + "\" was not injected: check your FXML file '" + getClass().getSimpleName() + ".fxml'.");
        }
    }
}
