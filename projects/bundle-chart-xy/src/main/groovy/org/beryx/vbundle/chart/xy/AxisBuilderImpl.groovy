package org.beryx.vbundle.chart.xy

import groovy.util.logging.Slf4j;

import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier

import javafx.util.StringConverter

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.fxui.chart.xy.AxisBuilder;

import javafx.scene.chart.Axis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.ValueAxis
import javafx.scene.chart.CategoryAxis

/**
 * Implementation of the {@link AxisBuilder} interface.
 */
@Slf4j
class AxisBuilderImpl<Q,A> implements AxisBuilder<Q,A> {
	String label
	boolean categorical
	Class defaultQueryResultClass = Double.class
	Function defaultDataConverter = {val -> val}

	Class axisDataClass = Double.class
	Function tickLabelProvider = {val -> "$val"}

	Closure configClosure

	public AxisBuilderImpl(Map options) {
		Class<Supplier<Class<?>[]>> type = options?.type
		label = options?.label
		categorical = options?.categorical
		configClosure = options?.config
		log.trace "Axis $label: type=$type"

		Supplier<Class<?>[]> typeSupplier = type.getConstructor().newInstance()
		(defaultQueryResultClass, axisDataClass) = typeSupplier.get();
		if(!defaultQueryResultClass) defaultQueryResultClass = categorical ? String : Double
		if(!axisDataClass) axisDataClass = defaultQueryResultClass

		defaultDataConverter = options?.converter
		if(!defaultDataConverter)  {
			if(java.util.Date.class.isAssignableFrom(defaultQueryResultClass)) {
				defaultDataConverter = {java.util.Date val -> (Long)(val.getTime())}
			} else {
				defaultDataConverter = {val -> val}
			}
		}

		tickLabelProvider = options?.formatter
		if(!tickLabelProvider) {
			String format = options?.format
			if(java.util.Date.class.isAssignableFrom(defaultQueryResultClass)) {
				SimpleDateFormat sdf = format ? new SimpleDateFormat(format) : new SimpleDateFormat();
				tickLabelProvider = {Number val -> sdf.format(new java.util.Date(val))}
			} else if(Number.class.isAssignableFrom(defaultQueryResultClass)) {
				DecimalFormat dfmt = format ? new DecimalFormat(format) : new DecimalFormat()
				tickLabelProvider = {val -> dfmt.format(val)}
			} else {
				tickLabelProvider = {val -> "$val"}
			}
		}
	}

	@Override
	public Class<Q> getDefaultQueryResultType() {
		return defaultQueryResultClass;
	}

	@Override
	public Function<Q, A> getDefaultDataConverter() {
		return defaultDataConverter;
	}

	@Override
	public Axis get() {
			Axis<?> axis = categorical ? new CategoryAxis() : new NumberAxis();
			axis.setLabel(label);

			axis.setAutoRanging(true);

			if(axis instanceof ValueAxis) {
				ValueAxis valueAxis = (ValueAxis) axis;
				valueAxis.setTickLabelFormatter(new StringConverter() {
					@Override public String toString(Object value) { return tickLabelProvider.apply(value) }
					@Override public Number fromString(String string) {return null;}
				});
			}
			if(axis instanceof NumberAxis) {
				NumberAxis numberAxis = (NumberAxis) axis;
				numberAxis.setForceZeroInRange(false);
			}

			configClosure?.call(axis)

			return axis;
	}
}
