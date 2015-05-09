package org.beryx.viewreka.fxui;

import java.util.Map;

import org.beryx.viewreka.fxui.chart.FxChartBuilder;
import org.beryx.viewreka.project.View;

public interface FxView extends View {
	// No additional methods
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, FxChartBuilder<?>> getChartBuilders();

}
