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
package org.beryx.viewreka.project;

import java.util.Map;

import org.beryx.viewreka.chart.ChartBuilder;
import org.beryx.viewreka.model.ViewModel;
import org.beryx.viewreka.parameter.Parameter;
import org.beryx.viewreka.settings.ViewSettings;

/**
 * This interface, which extends {@link ViewModel}, must be implemented by any view of a Viewreka project.
 */
public interface View extends ViewModel {
    ViewSettings getViewSettings();

    /**
     * Retrieves the parameter editor builder for the specified parameter.
     * @param parameter the parameter for which an editor builder is needed
     * @return the {@link ParameterEditorBuilder} for the specified parameter
     */
    <T, E extends ParameterEditor<T>, P> ParameterEditorBuilder<T,E,P> getParameterEditorBuilder(Parameter<T> parameter);

    /**
     * Retrieves the chart builders of this view.
     * @return a map of {@link ChartBuilder}s indexed by their names
     */
    <CB extends ChartBuilder<?,?>> Map<String, CB> getChartBuilders();

    /**
     * If a view has a chart parameter, then each possible value of this parameter will be associated with a series in a chart of this view.
     * @return the chart parameter
     */
    <T> Parameter<T> getChartParameter();
}
