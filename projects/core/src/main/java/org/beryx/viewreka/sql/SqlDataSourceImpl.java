package org.beryx.viewreka.sql;


public class SqlDataSourceImpl implements SqlDataSource {
	private final String name;
	private final SqlConnectionProvider connectionProvider;

	public SqlDataSourceImpl(String name, String url, String user, String password) {
		this.name = name;
		this.connectionProvider = new SqlConnectionProviderImpl(url, user, password);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public SqlConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	@Override
	public void close() throws Exception {
		if(connectionProvider != null) {
			connectionProvider.close();
		}
	}
}
