package org.beryx.viewreka.sql;

import java.sql.Connection;

/**
 * An SQL {@link Connection} provider.
 */
public interface SqlConnectionProvider extends AutoCloseable {
    /**
     * Retrieves an SQL {@link Connection}.
     * @return the SQL connection
     */
    Connection getConnection();
}
