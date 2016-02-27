package org.beryx.vbundle.chart.html
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import javafx.scene.web.WebEngine
import org.beryx.viewreka.core.ViewrekaException
import org.beryx.viewreka.dsl.BaseDelegate
import org.beryx.viewreka.model.DatasetProvider

import java.util.function.Consumer
/**
 * The closure delegate used by a {@link HtmlChartBuilderBuilder}.
 */
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
        int row = 0

        DataColumnProvider(Map options) {
            if(!options) throw new ViewrekaException('Null options')
            datasetProvider = options['dataset']
            if(!datasetProvider) throw new ViewrekaException('No dataset provided')
            column = options['column']
            def rowVal = options['row']
            row = (rowVal instanceof Number) ? ((Number)rowVal).intValue() : 0
        }

        String getData() {
            def data = null
            if(!column) {
                data = datasetProvider.getDataset().getObject(row, 1)
            } else if(column instanceof Number) {
                int col = ((Number)column).intValue()
                data = datasetProvider.getDataset().getObject(row, col)
            } else {
                String colName = String
                data = datasetProvider.getDataset().getObject(row, colName)
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
