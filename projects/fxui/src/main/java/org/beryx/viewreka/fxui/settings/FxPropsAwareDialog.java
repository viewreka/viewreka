package org.beryx.viewreka.fxui.settings;

import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.stage.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * This interface adds support for {@link javafx.scene.control.Dialog}s able to automatically retrieve and save various {@link javafx.beans.property.Property} values.
 */
public interface FxPropsAwareDialog<R> {
    static final Logger _log = LoggerFactory.getLogger(FxPropsAwareDialog.class);

    FxPropsManager getFxPropsManager();
    default Dialog<R> getDialog() {
        return (Dialog<R>)this;
    }
    default Optional<R> showAndWait() {
        return getDialog().showAndWait();
    }

    default Optional<R> showDialog() {
        Dialog<R> dialog = getDialog();

        FxPropsManager fxPropsManager = getFxPropsManager();
        GuiSettings settings = fxPropsManager.getSettings();
        String prefix = fxPropsManager.getPrefix();

        String widthKey = prefix + ".dialog.width";
        double width = settings.getProperty(widthKey, dialog.getDialogPane().getPrefWidth(), false);
        dialog.setWidth(width);
        String heightKey = prefix + ".dialog.height";
        double height = settings.getProperty(heightKey, dialog.getDialogPane().getPrefHeight(), false);
        dialog.setHeight(height);
        dialog.widthProperty().addListener((obs, oldVal, newVal) -> settings.setProperty(widthKey, newVal));
        dialog.heightProperty().addListener((obs, oldVal, newVal) -> settings.setProperty(heightKey, newVal));

        String xKey = prefix + ".dialog.x";
        double x = settings.getProperty(xKey, -1.0, false);
        String yKey = prefix + ".dialog.y";
        double y = settings.getProperty(yKey, -1.0, false);

        fxPropsManager.register(val -> dialog.setWidth((double)val), dialog::getWidth, "width", "dialog");
        fxPropsManager.register(val -> dialog.setHeight((double)val), dialog::getHeight, "height", "dialog");
        fxPropsManager.attachTo(dialog.getDialogPane().getScene().getWindow());

        dialog.setOnShown(ev -> {
            if(x >= 0 && y >= 0 && !Screen.getScreensForRectangle(x, y, x+1, y+1).isEmpty()) {
                Platform.runLater(() -> {
                    _log.debug("Setting position of dialog {} to ({}, {})", prefix, x, y);
                    dialog.setX(x);
                    dialog.setY(y);
                });
            }
        });
        dialog.setOnHiding(ev -> {
            settings.setProperty(xKey, dialog.getX());
            settings.setProperty(yKey, dialog.getY());
        });
        return showAndWait();
    }
}
