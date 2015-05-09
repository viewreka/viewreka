package org.beryx.viewreka.fxui.chart;

import java.util.List;
import java.util.function.Supplier;

import javafx.scene.layout.Pane;

import org.beryx.viewreka.chart.ChartBuilder;

public interface FxChartBuilder<D> extends ChartBuilder<D, Pane> {
	void setTitleSupplier(Supplier<String> titleSupplier);
	void setStylesheetSupplier(Supplier<String> titleSupplier);
	public void setCurrentChartStyle(String currentChartStyle);
	public List<String> getChartStyles();
}
