package org.beryx.viewreka.sql;

import java.sql.Connection;

public interface SqlConnectionProvider extends AutoCloseable {
	Connection getConnection();
}
