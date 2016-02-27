package org.beryx.viewreka.dsl.parameter;

import org.beryx.viewreka.dsl.transform.AliasHandler;

/**
 * The {@link AliasHandler} interface used by <code>parameter</code> type aliases
 * (such as <code>string</code>, <code>int</code>, <code>double</code>, <code>sqlDate</code> etc.).
 */
public interface ParameterHandler extends AliasHandler<ParameterBuilder<?,?,?>> {
}
