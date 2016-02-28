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
