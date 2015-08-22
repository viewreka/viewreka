package org.beryx.viewreka.fxui.chart.html;

import java.util.function.Consumer;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.beryx.viewreka.chart.ChartController;

/**
 * A chart controller that displays HTML in a {@link WebView}.
 * HtmlChartController does not create its own data. Instead, it relies on a {@link WebEngine} consumer that knows how to retrieve the content to be displayed.
 * Therefore, {@link #createChartData()} needs only a dummy implementation and {@link #displayChart(String)} ignores its {@code data} argument.
 */
public class HtmlChartController implements ChartController<String> {

    private final Consumer<WebEngine> webEngineConsumer;
    private final WebView webView = new WebView();

    /**
     * @param chartParentPane that should contain the {@link WebView} used by this chart controller
     * @param webEngineConsumer a {@link WebEngine} consumer that knows how to retrieve the content to be displayed, without relying on the value returned by {@link #createChartData()}
     */
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
