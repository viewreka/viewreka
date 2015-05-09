package org.beryx.viewreka.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.beryx.viewreka.core.ViewrekaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlConnectionProviderImpl implements SqlConnectionProvider {
	private static final Logger log = LoggerFactory.getLogger(SqlConnectionProviderImpl.class);

	private final String url;
	private final String user;
	private final String password;

	private Connection connection = null;

	public SqlConnectionProviderImpl(String url, String user, String password) {
		this.url = url;
		this.user = user;
		this.password = password;
	}

	@Override
	public Connection getConnection() {
		try {
			if((connection != null) && !connection.isValid(0)) {
				try {
					connection.close();
				} catch (Exception e) {
					log.warn("Error closing connection " + url, e);
				}
				connection = null;
			}
			if(connection == null) {
				connection = DriverManager.getConnection(url, user, password);
			}
			return connection;
		} catch (SQLException e) {
			throw new ViewrekaException("getConnection() failed", e);
		}
	}

	@Override
	public void close() throws SQLException {
		if(connection != null) {
			connection.close();
		}
	}
}
