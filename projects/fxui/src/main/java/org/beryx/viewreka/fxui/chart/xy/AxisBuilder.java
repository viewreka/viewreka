package org.beryx.viewreka.fxui.chart.xy;

import java.util.function.Function;
import java.util.function.Supplier;

import javafx.scene.chart.Axis;

/**
 * An {@link Axis} supplier that also provides methods linking axis values with dataset values.
 * @param <Q> the type of the dataset column(s) associated with the data displayed on this axis
 * @param <A> the type of data displayed on this axis
 */
public interface AxisBuilder<Q,A> extends Supplier<Axis<A>>{
	/**
	 * @return the type of the dataset column(s) associated with the data displayed on this axis
	 */
	Class<Q> getDefaultQueryResultType();

	/**
	 * @return a function able to convert data from the associated dataset column(s) to the type of data displayed on this axis
	 */
	Function<Q, A> getDefaultDataConverter();
}
