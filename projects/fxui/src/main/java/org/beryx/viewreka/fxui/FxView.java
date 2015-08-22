package org.beryx.viewreka.fxui;

import java.util.Map;

import org.beryx.viewreka.fxui.chart.FxChartBuilder;
import org.beryx.viewreka.project.View;

/**
 * An extension of the {@link View} interface for views used in JavaFX Viewreka projects.
 */
public interface FxView extends View {
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, FxChartBuilder<?>> getChartBuilders();

}
