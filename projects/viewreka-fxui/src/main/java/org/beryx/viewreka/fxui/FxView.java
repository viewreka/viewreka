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
package org.beryx.viewreka.fxui;

import java.util.Map;

import org.beryx.viewreka.fxui.chart.FxChartBuilder;
import org.beryx.viewreka.project.View;

/**
 * An extension of the {@link View} interface for views used in JavaFX Viewreka projects.
 */
public interface FxView extends View {
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, FxChartBuilder<?>> getChartBuilders();

}
