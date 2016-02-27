package org.beryx.viewreka.dsl.chart;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

import org.beryx.viewreka.dsl.transform.AliasHandler;

/**
 * The {@link AliasHandler} interface used by <code>xAxis</code> or <code>yAxis</code> type aliases
 * (such as <code>string</code>, <code>int</code>, <code>double</code>, <code>sqlDate</code> etc.).
 */
public interface AxisHandler extends AliasHandler<Supplier<Class<?>[]>>{
}
