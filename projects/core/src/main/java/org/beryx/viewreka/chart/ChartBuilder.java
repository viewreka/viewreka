package org.beryx.viewreka.chart;

import org.beryx.viewreka.project.View;


/**
 * The interface used by a {@link View} in order to create a {@link ChartController}
 * @param <D> The type of the chart data used by the {@link ChartController}
 * @param <P> The type of the parent UI component (that is, the component having as child the chart associated with the {@link ChartController})
 */
@FunctionalInterface
public interface ChartBuilder<D,P> {
	/**
	 * Creates a {@link ChartController} with a given parent
	 * @param chartParentPane the UI component having as child the chart associated with the {@link ChartController}
	 * @return the {@link ChartController}
	 */
    ChartController<D> createController(P chartParentPane);
}
