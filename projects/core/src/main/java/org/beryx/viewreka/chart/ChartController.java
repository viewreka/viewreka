package org.beryx.viewreka.chart;

/**
 * The interface responsible for creating and displaying the chart data.
 * It is assumed that an instance of this interface has an associated chart, but the interface itself makes no direct reference to it.
 * @param <D> The type of the data used by the chart
 */
public interface ChartController<D> {
    /**
     * Creates the data used by the chart.
     * This method may perform a long-running operation and it is typically called from a background thread.
     * @return the data needed to display the chart
     */
    D createChartData();

    /**
     * Displays the chart using the data provided as argument.
     * This method is typically called from the UI thread and therefore it should not perform long-running operations.
     * @param data the data used to display the chart
     */
    void displayChart(D data);
}
