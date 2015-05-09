package org.beryx.viewreka.fxui.chart.xy;

import java.util.function.Supplier;

import org.beryx.viewreka.fxui.chart.FxChartBuilder;

public abstract class AbstractChartBuilder<D> implements FxChartBuilder<D> {
	private Supplier<String> titleSupplier;
	private Supplier<String> stylesheetSupplier;
	private String currentChartStyle;

	public Supplier<String> getTitleSupplier() {
		return titleSupplier;
	}
	@Override
	public void setTitleSupplier(Supplier<String> titleSupplier) {
		this.titleSupplier = titleSupplier;
	}

	public Supplier<String> getStylesheetSupplier() {
		return stylesheetSupplier;
	}
	@Override
	public void setStylesheetSupplier(Supplier<String> stylesheetSupplier) {
		this.stylesheetSupplier = stylesheetSupplier;
	}

	public String getCurrentChartStyle() {
		return currentChartStyle;
	}
	@Override
	public void setCurrentChartStyle(String currentChartStyle) {
		this.currentChartStyle = currentChartStyle;
	}

}
