package org.beryx.viewreka.project;

import java.util.Map;

import org.beryx.viewreka.chart.ChartBuilder;
import org.beryx.viewreka.model.ViewModel;
import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.settings.ViewSettings;

public interface View extends ViewModel {
	ViewSettings getViewSettings();

	<T, E extends ParameterEditor<T>, P> ParameterEditorBuilder<T,E,P> getParameterEditorBuilder(Parameter<T> parameter);

	<CB extends ChartBuilder<?,?>> Map<String, CB> getChartBuilders();
	<T> Parameter<T> getChartParameter();
}
