package org.beryx.viewreka.core;

import java.util.function.Supplier;

/**
 * A functional interface similar to {@link Supplier}, but allowing to throw an exception.
 * @param <T> the type of results supplied by this supplier
 */
@FunctionalInterface
public interface SupplierWithException<T> {
    /**
     * Gets a result.
     * @return a result
     * @throws Exception
     */
    T get() throws Exception;
}
