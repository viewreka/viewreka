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

import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.Parameter;

/**
 * The configuration of a chart series
 * @param <X> the type of the X axis
 * @param <Y> the type of the Y axis
 */
public interface SeriesConfig<X,Y> {
	/**
	 * @return the data provider for this series configuration
	 */
	DatasetProvider getDatasetProvider();

	/**
	 * @return the index of the dataset column providing data for the X axis
	 */
	int getXColumn();

	/**
	 * @return the name of the dataset column providing data for the X axis
	 */
	String getXColumnName();

	/**
	 * @return the index of the dataset column providing data for the Y axis
	 */
	int getYColumn();

	/**
	 * @return the name of the dataset column providing data for the Y axis
	 */
	String getYColumnName();

	/**
	 * @return the index of the dataset column providing extra data for the Y axis
	 */
	int getExtraColumn();

	/**
	 * @return the name of the dataset column providing extra data for the Y axis
	 */
	String getExtraColumnName();

	/**
	 * @return the chart parameter of the corresponding view
	 */
	Parameter<?> getChartParameter();

	/**
	 * @return the type of the dataset column(s) associated with the X axis
	 */
	Class<?> getXColumnType();

	/**
	 * @return the type of the dataset column(s) associated with the Y axis
	 */
	Class<?> getYColumnType();

	/**
	 * @return a function able to convert data from the dataset column(s) associated with the X axis to the type of data displayed on the X axis
	 */
	Function<?, X> getXDataConverter();

	/**
	 * @return a function able to convert data from the dataset column(s) associated with the Y axis to the type of data displayed on the Y axis
	 */
	Function<?, Y> getYDataConverter();
}
