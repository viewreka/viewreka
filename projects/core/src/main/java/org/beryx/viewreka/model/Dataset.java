package org.beryx.viewreka.model;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.core.SupplierWithException;



public interface Dataset extends Parameterized {

	String getName();

	void close() throws ViewrekaException;

	int getItemCount();

	int getColumnCount();

	String getColumnName(int column) throws ViewrekaException;
	int getColumn(String columnName) throws ViewrekaException;

	<T> T rawGet(int column, int index, Class<T> type) throws Exception;
	Object rawGet(int column, int index) throws Exception;

	default <T> T get(int column, int index, SupplierWithException<T> supplier) {
		if((column <= 0) || (column > getColumnCount())) {
			throw new ViewrekaException(getName() + ": Column out of bounds while retrieving item at (col: " + column + ", idx: " + index + "). columnCount = " + getColumnCount());
		}
		if((index < 0) || (index >= getItemCount())) {
			throw new ViewrekaException(getName() + ": Index out of bounds while retrieving item at (col: " + column + ", idx: " + index + "). itemCount = " + getItemCount());
		}
		try {
			return supplier.get();
		} catch (Exception e) {
			throw new ViewrekaException(getName() + ": Cannot retrieve item at (col: " + column + ", idx: " + index + ")", e);
		}
	}

	default <T> T getValue(int column, int index, Class<T> type) throws ViewrekaException {
		return get(column, index, () -> rawGet(column, index, type));
	}
	default <T> T getValue(String columnName, int index, Class<T> type) throws ViewrekaException {
		return getValue(getColumn(columnName), index, type);
	}

	default Object getObject(int column, int index) throws ViewrekaException {
		return get(column, index, () -> rawGet(column, index));
	}
	default Object getObject(String columnName, int index) throws ViewrekaException {
		return getObject(getColumn(columnName), index);
	}


	default String getString(int column, int index) throws ViewrekaException {
		return getValue(column, index, String.class);
	}

	default String getString(String columnName, int index) throws ViewrekaException {
		return getValue(columnName, index, String.class);
	}

	default String getInfo() {
		StringBuilder sb = new StringBuilder(128);
		sb.append(getName()).append(':');
		sb.append(getItemCount()).append(" elements of type (");
		String sep = "";
		int columnCount = getColumnCount();
		for(int column = 1; column <= columnCount; column++) {
			sb.append(sep).append(getColumnName(column)) ;
			sep = ", ";
		}
		sb.append("), parameters: " + getParameterNames());
		return sb.toString();
	}

}
