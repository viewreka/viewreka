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
package org.beryx.viewreka.fxui.chart.html;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.beryx.viewreka.fxui.chart.FxChartBuilder;

/**
 * A builder that creates HTML charts (charts with string content displayed by a {@link WebView}).
 */
public class HtmlChartBuilder implements FxChartBuilder<String> {
    private final Consumer<WebEngine> webEngineConsumer;

    /**
     * Constructs an HtmlChartBuilder
     * @param webEngineConsumer a consumer responsible for loading the HTML chart content into a {@link WebEngine}
     */
    public HtmlChartBuilder(Consumer<WebEngine> webEngineConsumer) {
        this.webEngineConsumer = webEngineConsumer;
    }

    @Override
    public HtmlChartController createController(Pane chartParentPane) {
        return new HtmlChartController(chartParentPane, webEngineConsumer);
    }

    @Override
    public void setTitleSupplier(Supplier<String> titleSupplier) {
        // not used
    }

    @Override
    public void setStylesheetSupplier(Supplier<String> stylesheetSupplier) {
        // not used
    }

    @Override
    public void setCurrentChartStyle(String currentChartStyle) {
        // not used
    }

    @Override
    public List<String> getChartStyles() {
        return Collections.emptyList();
    }
}
