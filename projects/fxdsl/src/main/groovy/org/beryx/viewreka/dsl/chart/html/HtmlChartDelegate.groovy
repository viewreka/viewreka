package org.beryx.viewreka.dsl.chart.html

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.Consumer;

import groovy.transform.Immutable;
import groovy.transform.ToString;
import groovy.util.logging.Slf4j;
import javafx.scene.chart.AreaChart
import javafx.scene.chart.Axis
import javafx.scene.chart.BarChart
import javafx.scene.chart.BubbleChart
import javafx.scene.chart.LineChart
import javafx.scene.chart.ScatterChart
import javafx.scene.chart.StackedAreaChart
import javafx.scene.chart.StackedBarChart
import javafx.scene.chart.XYChart
import javafx.scene.web.WebEngine;

import org.beryx.viewreka.core.ViewrekaException
import org.beryx.viewreka.dsl.BaseDelegate
import org.beryx.viewreka.fxui.chart.html.HtmlChartBuilder;
import org.beryx.viewreka.fxui.chart.xy.SeriesConfig
import org.beryx.viewreka.fxui.chart.xy.XYChartBuilder
import org.beryx.viewreka.fxui.chart.xy.XYChartCreator
import org.beryx.viewreka.model.DatasetProvider

@Slf4j
@ToString
class HtmlChartDelegate extends BaseDelegate {
	final String name;
	private content
	private contentType

	@ToString
	private static class DataColumnProvider {
		DatasetProvider datasetProvider;
		def column = 1;
		int index = 0

		DataColumnProvider(Map options) {
			if(!options) throw new ViewrekaException('Null options')
			datasetProvider = options['dataset']
			if(!datasetProvider) throw new ViewrekaException('No dataset provided')
			column = options['column']
			def idxVal = options['index']
			index = (idxVal instanceof Number) ? ((Number)idxVal).intValue() : 0
		}

		String getData() {
			def data = null
			if(!column) {
				data = datasetProvider.getDataset().getObject(1, index)
			} else if(column instanceof Number) {
				int col = ((Number)column).intValue()
				data = datasetProvider.getDataset().getObject(col, index)
			} else {
				String colName = String
				data = datasetProvider.getDataset().getObject(colName, index)
			}
		}
	}

	public HtmlChartDelegate(String name, Map<String, DatasetProvider> dataSetProviders) {
		this.name = name
		injectProperties(dataSetProviders)
	}

	URL file(String path) {
		return new File(path).toURI().normalize().toURL()
	}

	URL url(String spec) {
		return spec.toURL()
	}

	DataColumnProvider data(Map options) {
		return new DataColumnProvider(options)
	}

	Consumer<WebEngine> getWebEngineConsumer() {
		return { WebEngine webEngine ->
			if(content instanceof URL) {
				webEngine.load(content.toString())
			} else {
				String webContentType = (contentType instanceof DataColumnProvider) ? contentType.getData() : "${contentType ?: 'text/html'}" as String
				String webContent = (content instanceof DataColumnProvider) ? content.getData() : "${content ?: ''}" as String
				webEngine.loadContent(webContent, webContentType)
			}
		}
	}
}
