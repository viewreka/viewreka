package org.beryx.viewreka.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.core.SupplierWithException;
import org.beryx.viewreka.model.Dataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlDataset implements Dataset {
	private static final Logger log = LoggerFactory.getLogger(SqlDataset.class);

	private final String name;
	private final ResultSet resultSet;
	private final PreparedStatement statement;
	private final Set<String> parameterNames;
	private final int itemCount;
	private final int columnCount;
	private final Map<String, Integer> columns = new LinkedHashMap<>();

	private final Object rsLock = new Object();

	public SqlDataset(String name, ResultSet resultSet, Set<String> parameterNames, PreparedStatement statement) {
		this.name = name;
		this.resultSet = resultSet;
		this.statement = statement;
		this.parameterNames = parameterNames;
		try {
			resultSet.beforeFirst();
			boolean valid = resultSet.last();
			itemCount = valid ? resultSet.getRow() : 0;
			ResultSetMetaData metaData = resultSet.getMetaData();
			columnCount = metaData.getColumnCount();
			for(int i=1; i<=columnCount; i++) {
				columns.put(metaData.getColumnName(i).toUpperCase(), i);
			}
			log.debug("SqlDataset {} with {} rows and {} columns created.", name, itemCount, columnCount);
		} catch (SQLException e) {
			throw new ViewrekaException("Failed to create dataset " + name, e);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void close() throws ViewrekaException {
		try {
			log.debug("Closing dataset " + name);
			statement.close();
		} catch(SQLException e) {
			throw new ViewrekaException("Error closing dataset " + name, e);
		}
	}

	@Override
	public Set<String> getParameterNames() {
		return parameterNames;
	}

	@Override
	public int getItemCount() {
		return itemCount;
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	@Override
	public String getColumnName(int column) throws ViewrekaException {
		try {
			synchronized(rsLock) {
				return resultSet.getMetaData().getColumnName(column);
			}
		} catch (SQLException e) {
			throw new ViewrekaException("getColumnName(" + column + ") failed for dataset " + name, e);
		}
	}

	@Override
	public int getColumn(String columnName) throws ViewrekaException {
		if(columnName == null) throw new ViewrekaException("Column name is null.");
		Integer column = columns.get(columnName.toUpperCase());
		if(column == null) throw new ViewrekaException("Unknown column name: '" + columnName + "'");
		return column;
	}

	@Override
	public <T> T rawGet(int column, int index, Class<T> type) throws Exception {
		try {
			synchronized(rsLock) {
				moveTo(index);
				return resultSet.getObject(column, type);
			}
		} catch (SQLException e) {
			throw new ViewrekaException("rawGet(" + column + ", " + index + ", " + type + ") failed for dataset " + name, e);
		}
	}
	public <T> T rawGet(String column, int index, Class<T> type) throws Exception {
		try {
			synchronized(rsLock) {
				moveTo(index);
				return resultSet.getObject(column, type);
			}
		} catch (SQLException e) {
			throw new ViewrekaException("rawGet(" + column + ", " + index + ", " + type + ") failed for dataset " + name, e);
		}
	}

	@Override
	public Object rawGet(int column, int index) throws Exception {
		try {
			synchronized(rsLock) {
				moveTo(index);
				return resultSet.getObject(column);
			}
		} catch (SQLException e) {
			throw new ViewrekaException("rawGet(" + column + ", " + index + ") failed for dataset " + name, e);
		}
	}
	public Object rawGet(String column, int index) throws Exception {
		try {
			synchronized(rsLock) {
				moveTo(index);
				return resultSet.getObject(column);
			}
		} catch (SQLException e) {
			throw new ViewrekaException("rawGet(" + column + ", " + index + ") failed for dataset " + name, e);
		}
	}


	<T> T get(String columnName, int index, SupplierWithException<T> supplier) {
		if(columnName == null) {
			throw new ViewrekaException(getName() + ": Null column name while retrieving item at index: " + index + ".");
		}
		if((index < 0) || (index >= getItemCount())) {
			throw new ViewrekaException(getName() + ": Index out of bounds while retrieving item at (col: " + columnName + ", idx: " + index + "). itemCount = " + getItemCount());
		}
		try {
			return supplier.get();
		} catch (Exception e) {
			throw new ViewrekaException(getName() + ": Cannot retrieve item at (col: " + columnName + ", idx: " + index + ")", e);
		}
	}


	@Override
	public <T> T getValue(String columnName, int index, Class<T> type) throws ViewrekaException {
		return get(columnName, index, () -> rawGet(columnName, index, type));
	}

	@Override
	public Object getObject(String columnName, int index) throws ViewrekaException {
		return get(columnName, index, () -> rawGet(columnName, index));
	}

	private boolean moveTo(int index) throws Exception {
		synchronized(rsLock) {
			int currIndex = resultSet.getRow();
			if(currIndex <= 0) {
				if(!resultSet.first()) {
					throw new SQLException("Trying to move to index " + index + " of an empty resultSet.");
				}
				currIndex = 1;
			}
			int offset = (index + 1) - currIndex;
			boolean ok = resultSet.relative(offset);
			if(!ok) {
				throw new SQLException("Cannot move to index " + index);
			}
			return ok;
		}
	}

	@Override
	public String toString() {
		return getInfo();
	}
}
