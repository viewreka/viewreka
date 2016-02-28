/**
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
