package org.beryx.viewreka.core;

public class ViewrekaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new Viewreka exception with the specified detail message.
     * @param message the detail message
     */
    public ViewrekaException(String message) {
        super(message);
    }

    /**
     * Constructs a new Viewreka exception with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public ViewrekaException(String message, Throwable cause) {
        super(message, cause);
    }
}
