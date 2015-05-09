package org.beryx.viewreka.core;

public class ViewrekaException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ViewrekaException(String message) {
		super(message);
	}

	public ViewrekaException(String message, Throwable cause) {
		super(message, cause);
	}
}
