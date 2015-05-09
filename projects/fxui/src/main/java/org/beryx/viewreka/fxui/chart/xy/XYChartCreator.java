package org.beryx.viewreka.fxui.chart.xy;

import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;

public abstract class XYChartCreator {
	private final String style;

	public abstract <X, Y> XYChart<X, Y> createChart(Axis<X> xAxis, Axis<Y> yAxis);

	public XYChartCreator(String style) {
		this.style = style;
	}

	public String getStyle() {
		return style;
	}

}
