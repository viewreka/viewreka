package org.beryx.viewreka.chart;

public interface ChartController<D> {
	D createChartData();
	void displayChart(D data);
}
