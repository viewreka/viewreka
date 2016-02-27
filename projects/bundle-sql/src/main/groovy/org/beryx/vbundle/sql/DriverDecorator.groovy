package org.beryx.vbundle.sql

import java.sql.Driver
/**
 * Helper class used to avoid class loader problems.
 */
public class DriverDecorator implements Driver {
    @Delegate
    private final Driver delegate;

    public DriverDecorator(Driver delegate) {
        this.delegate = delegate;
    }
}
