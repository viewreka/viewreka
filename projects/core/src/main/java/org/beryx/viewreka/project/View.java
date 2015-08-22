package org.beryx.viewreka.project;

import java.util.Map;

import org.beryx.viewreka.chart.ChartBuilder;
import org.beryx.viewreka.model.ViewModel;
import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.settings.ViewSettings;

/**
 * This interface, which extends {@link ViewModel}, must be implemented by any view of a Viewreka project.
 */
public interface View extends ViewModel {
    ViewSettings getViewSettings();

    /**
     * Retrieves the parameter editor builder for the specified parameter.
     * @param parameter the parameter for which an editor builder is needed
     * @return the {@link ParameterEditorBuilder} for the specified parameter
     */
    <T, E extends ParameterEditor<T>, P> ParameterEditorBuilder<T,E,P> getParameterEditorBuilder(Parameter<T> parameter);

    /**
     * Retrieves the chart builders of this view.
     * @return a map of {@link ChartBuilder}s indexed by their names
     */
    <CB extends ChartBuilder<?,?>> Map<String, CB> getChartBuilders();

    /**
     * If a view has a chart parameter, then each possible value of this parameter will be associated with a series in a chart of this view.
     * @return the chart parameter
     */
    <T> Parameter<T> getChartParameter();
}
