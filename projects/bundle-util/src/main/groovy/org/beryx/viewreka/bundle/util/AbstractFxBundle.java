package org.beryx.viewreka.bundle.util;

import org.beryx.viewreka.bundle.api.ViewrekaBundle;
import org.beryx.viewreka.fxui.FxGui;

public abstract class AbstractFxBundle implements ViewrekaBundle<FxGui> {
    @Override
    public void addTo(FxGui fxGui) {
        // Default empty implementation
    }

    @Override
    public void removeFrom(FxGui fxGui) {
        // Default empty implementation
    }
}
