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
package org.beryx.viewreka.fxui.chart;

import java.util.List;
import java.util.function.Supplier;

import javafx.scene.layout.Pane;

import org.beryx.viewreka.chart.ChartBuilder;
import org.beryx.viewreka.chart.ChartController;

/**
 * A chart builder for JavaFx charts, which uses a JavaFx {@link Pane} as parent UI component.
 * @param <D> The type of the chart data used by the {@link ChartController}
 */
public interface FxChartBuilder<D> extends ChartBuilder<D, Pane> {
    /**
     * Sets the title supplier for the charts created by this builder.
     * @param titleSupplier the title supplier for the charts created by this builder
     */
    void setTitleSupplier(Supplier<String> titleSupplier);

    /**
     * Sets the stylesheet supplier for the charts created by this builder.
     * @param stylesheetSupplier the stylesheet supplier for the charts created by this builder
     */
    void setStylesheetSupplier(Supplier<String> stylesheetSupplier);

    /**
     * Sets the chart style to be used when creating a new chart
     * @param currentChartStyle the chart style to be used when creating a new chart
     */
    public void setCurrentChartStyle(String currentChartStyle);

    /**
     * @return the list of chart styles allowed by this builder
     */
    public List<String> getChartStyles();
}
