package org.beryx.viewreka.chart;


public interface ChartBuilder<D,P> {
	ChartController<D> createController(P chartParentPane);
}
