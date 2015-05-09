package org.beryx.viewreka.dsl.data

import java.sql.Driver;
import java.sql.DriverManager;

import groovy.lang.Closure
import groovy.transform.ToString;

import org.beryx.viewreka.model.DataSource;
import org.beryx.viewreka.model.Query;
import org.beryx.viewreka.sql.SqlDataSourceImpl;

import static org.beryx.viewreka.core.Util.requireNonNull;

class SqlDataSourceBuilder implements DataSourceBuilder {
	final SqlQueryBuilder queryBuilder = new SqlQueryBuilder()

	@ToString(includePackage=false)
	static class ConnectionInfo {
		String driver
		String connection
		String user
		String password
	}

	@Override
	public <Q extends Query> DataSource<Q> build(String dsName, Closure dsClosure) {
		def connInfo = new ConnectionInfo()
		dsClosure.delegate = connInfo
		dsClosure.resolveStrategy = Closure.DELEGATE_FIRST
		dsClosure.call()

		String sqlDriver = requireNonNull(connInfo.driver, "driver");
		String connection = requireNonNull(connInfo.connection, "connection");

		Class<?> driverClass = Class.forName(sqlDriver, true, Thread.currentThread().getContextClassLoader());
		Driver driver = (Driver)driverClass.getConstructor().newInstance();
		DriverManager.registerDriver(new DriverDecorator(driver));

		return new SqlDataSourceImpl(dsName, connection, connInfo.user, connInfo.password);
	}

	@Override
	public QueryBuilder getQueryBuilder() {
		return queryBuilder;
	}

}
