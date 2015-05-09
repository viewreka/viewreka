package org.beryx.viewreka.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.model.Dataset;
import org.beryx.viewreka.model.DatasetProvider;
import org.beryx.viewreka.parameter.ParameterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlDatasetProvider implements DatasetProvider {
	private static final Logger log = LoggerFactory.getLogger(SqlDatasetProvider.class);

	private final String name;
	private final SqlConnectionProvider connectionProvider;
	private final SqlQuery query;
	private final ParameterGroup globalParameterGroup;

	private boolean dirty = true;
	private SqlDataset cachedDataset = null;
	private final List<Consumer<DatasetProvider>> dirtyListeners = new ArrayList<>();

	public SqlDatasetProvider(String name, SqlConnectionProvider connectionProvider, SqlQuery query, ParameterGroup globalParameterGroup) {
		this.name = name;
		this.connectionProvider = connectionProvider;
		this.query = query;
		this.globalParameterGroup = globalParameterGroup;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<String> getParameterNames() {
		return query.getParameterNames();
	}

	@Override
	public synchronized void setDirty() {
		log.trace("Setting dirty: {} {}", name, query);
		this.dirty = true;
		dirtyListeners.forEach(listener -> listener.accept(this));
	}

	@SuppressWarnings("resource")
	@Override
	public synchronized Dataset getDataset() {
		if(dirty || (cachedDataset == null)) {
			if(cachedDataset != null) {
				cachedDataset.close();
			}

			PreparedStatement statement = null;
			try {
				Connection connection = connectionProvider.getConnection();
				statement = query.getPreparedStatement(connection, globalParameterGroup);
				ResultSet rs = statement.executeQuery();
				cachedDataset = new SqlDataset(name, rs, query.getParameterNames(), statement);
				dirty = false;
			} catch (SQLException e) {
				throw new ViewrekaException("Failed to retrieve the dataset for: " + query, e);
			}
		}
		return cachedDataset;
	}

	@Override
	public boolean addDirtyListener(Consumer<DatasetProvider> listener) {
		return dirtyListeners.add(listener);
	}

	@Override
	public boolean removeDirtyListener(Consumer<DatasetProvider> listener) {
		return dirtyListeners.remove(listener);
	}

	@Override
	public void close() throws Exception {
		connectionProvider.close();
	}

	@Override
	public String toString() {
		return name + ": " + getParameterNames();
	}
}