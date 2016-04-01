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
package org.beryx.viewreka.fxui.settings;

import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This interface adds support for constructing dialog boxes able to automatically retrieve and save various {@link javafx.beans.property.Property} values.
 */
public interface FxPropsAwareWindow {
    FxPropsManager getFxPropsManager();

    default Region getMainRegion() {
        return (Region)this;
    }

    default StageStyle getStageStyle() {
        return StageStyle.UTILITY;
    }

    default void showDialog(String title) {
        Region region = getMainRegion();

        Stage stage = new Stage();
        stage.initStyle(getStageStyle());
        stage.initModality(Modality.APPLICATION_MODAL);
        if(region.getMinWidth() > 0) {
            stage.setMinWidth(region.getMinWidth());
        }
        if(region.getMaxWidth() > 0) {
            stage.setMaxWidth(region.getMaxWidth());
        }
        if(region.getMinHeight() > 0) {
            stage.setMinHeight(region.getMinHeight());
        }
        if(region.getMaxHeight() > 0) {
            stage.setMaxHeight(region.getMaxHeight());
        }
        stage.setTitle(title);

        FxPropsManager fxPropsManager = getFxPropsManager();
        GuiSettings settings = fxPropsManager.getSettings();
        String prefix = fxPropsManager.getPrefix();

        String widthKey = prefix + ".dialog.width";
        double width = settings.getProperty(widthKey, region.getPrefWidth(), false);
        String heightKey = prefix + ".dialog.height";
        double height = settings.getProperty(heightKey, region.getPrefHeight(), false);
        Scene scene = new Scene(region, width, height);
        scene.widthProperty().addListener((obs, oldVal, newVal) -> settings.setProperty(widthKey, newVal));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> settings.setProperty(heightKey, newVal));

        stage.setScene(scene);

        String xKey = prefix + ".dialog.x";
        double x = settings.getProperty(xKey, -1.0, false);
        stage.xProperty().addListener((obs, oldVal, newVal) -> settings.setProperty(xKey, newVal));
        String yKey = prefix + ".dialog.y";
        double y = settings.getProperty(yKey, -1.0, false);
        stage.yProperty().addListener((obs, oldVal, newVal) -> settings.setProperty(yKey, newVal));
        if(x >= 0 && y >= 0 && !Screen.getScreensForRectangle(x, y, x+1, y+1).isEmpty()) {
            stage.setX(x);
            stage.setY(y);
        }

        fxPropsManager.register(val -> stage.setWidth((double)val), stage::getWidth, "width", "dialog");
        fxPropsManager.register(val -> stage.setHeight((double)val), stage::getHeight, "height", "dialog");
        fxPropsManager.attachTo(stage);

        stage.showAndWait();
    }
}
