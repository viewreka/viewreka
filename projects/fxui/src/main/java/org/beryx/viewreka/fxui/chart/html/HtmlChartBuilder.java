package org.beryx.viewreka.fxui.chart.html;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;

import org.beryx.viewreka.chart.ChartController;
import org.beryx.viewreka.fxui.chart.FxChartBuilder;

public class HtmlChartBuilder implements FxChartBuilder<String> {
	private final Consumer<WebEngine> webEngineConsumer;

	public HtmlChartBuilder(Consumer<WebEngine> webEngineConsumer) {
		this.webEngineConsumer = webEngineConsumer;
	}

	@Override
	public ChartController<String> createController(Pane chartParentPane) {
		return new HtmlChartController(chartParentPane, webEngineConsumer);
	}

	@Override
	public void setTitleSupplier(Supplier<String> titleSupplier) {
		// not used
	}

	@Override
	public void setStylesheetSupplier(Supplier<String> titleSupplier) {
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
