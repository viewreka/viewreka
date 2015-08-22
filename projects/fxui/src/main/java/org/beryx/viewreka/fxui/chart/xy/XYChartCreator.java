package org.beryx.viewreka.fxui.chart.xy;

import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;

/**
 * Abstract builder for creating XY charts.
 */
public abstract class XYChartCreator {
	private final String style;

	/**
	 * Creates an XY chart for the specified X and Y axes.
	 * @param xAxis the X axis
	 * @param yAxis the Y axis
	 * @return the newly created XY chart
	 */
	public abstract <X, Y> XYChart<X, Y> createChart(Axis<X> xAxis, Axis<Y> yAxis);

	/**
	 * @param style a string identifying the style of the charts created by this builder (for example: "line", "bar", "stackedArea" etc.)
	 */
	public XYChartCreator(String style) {
		this.style = style;
	}

	/**
	 * @return the style of of the charts created by this builder (for example: "line", "bar", "stackedArea" etc.)
	 */
	public String getStyle() {
		return style;
	}

}
