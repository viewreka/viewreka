package org.beryx.viewreka.core;

@FunctionalInterface
public interface SupplierWithException<T> {
	T get() throws Exception;
}
