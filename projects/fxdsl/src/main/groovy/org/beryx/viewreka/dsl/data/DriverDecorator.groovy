package org.beryx.viewreka.dsl.data

import groovy.lang.Delegate;

import java.sql.Driver;

public class DriverDecorator implements Driver {
	@Delegate
	private final Driver delegate;

	public DriverDecorator(Driver delegate) {
		this.delegate = delegate;
	}
}
