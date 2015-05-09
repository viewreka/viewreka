package org.beryx.viewreka.fxui.chart.xy;

import java.util.function.Function;
import java.util.function.Supplier;

import javafx.scene.chart.Axis;

public interface AxisBuilder<Q,A> extends Supplier<Axis<A>>{
	Class<Q> getDefaultQueryResultType();
	Function<Q, A> getDefaultDataConverter();
}
