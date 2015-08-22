package org.beryx.viewreka.sql;


/**
 * The default implementation of {@link SqlDataSource}.
 */
public class SqlDataSourceImpl implements SqlDataSource {
    private final String name;
    private final SqlConnectionProvider connectionProvider;

    /**
     * Constructs an SQL data source with the specified name and connection parameters.
     * @param name the data source name
     * @param url a database url of the form jdbc:subprotocol:subname
     * @param user the database user on whose behalf the connection is being made
     * @param password the user's password
     */
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
        connectionProvider.close();
    }
}
