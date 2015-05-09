package org.beryx.viewreka.fxui.chart.html;

import java.util.function.Consumer;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.beryx.viewreka.chart.ChartController;

public class HtmlChartController implements ChartController<String> {

	private final Consumer<WebEngine> webEngineConsumer;
	private final WebView webView = new WebView();

	public HtmlChartController(Pane chartParentPane, Consumer<WebEngine> webEngineConsumer) {
		this.webEngineConsumer = webEngineConsumer;

		chartParentPane.getChildren().add(webView);
		chartParentPane.widthProperty().addListener((obs, oldVal, newVal) -> {webView.setPrefWidth(newVal.doubleValue());});
		chartParentPane.heightProperty().addListener((obs, oldVal, newVal) -> {webView.setPrefHeight(newVal.doubleValue());});
	}

	@Override
	public String createChartData() {
		return "";
	}

	@Override
	public void displayChart(String data) {
		webEngineConsumer.accept(webView.getEngine());
	}
}
