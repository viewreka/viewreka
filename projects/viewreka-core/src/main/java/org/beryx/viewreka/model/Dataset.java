/**
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.beryx.viewreka.model;

import org.beryx.viewreka.core.ViewrekaException;
import org.beryx.viewreka.core.SupplierWithException;


/**
 * A table with data organized in rows and columns. Rows are numbered starting with 0. Columns may be referenced by number (starting with 1) or by name.
 */
public interface Dataset extends Parameterized, AutoCloseable {

    /**
     * @return the name of this dataset
     */
    String getName();

    @Override
    void close() throws ViewrekaException;

    /**
     * @return true, if this dataset is closed
     */
    boolean isClosed();

    /**
     * @return the number of rows in this dataset
     */
    int getRowCount();

    /**
     * @return the number of columns in this dataset
     */
    int getColumnCount();

    /**
     * Retrieves the name of a column specified by its index.
     * @param column the column index (the first column has index 1)
     * @return the name of the specified column
     * @throws ViewrekaException
     */
    String getColumnName(int column) throws ViewrekaException;

    /**
     * Retrieves the index of a column specified by its name.
     * @param columnName the name of the column
     * @return the column index (the first column has index 1)
     * @throws ViewrekaException
     */
    int getColumn(String columnName) throws ViewrekaException;

    /**
     * Retrieves the item found at a specified row and column, without checking the validity of the specified row and column.
     * @param row the row index (starting with 0)
     * @param column the column index (starting with 1)
     * @param type the expected type of the item to be retrieved
     * @return the item at the specified row and column
     * @throws Exception
     */
    <T> T rawGet(int row, int column, Class<T> type) throws Exception;

    /**
     * Retrieves the object found at a specified row and column, without checking the validity of the specified row and column.
     * @param row the row index (starting with 0)
     * @param column the column index (starting with 1)
     * @return the object found at the specified row and column.
     * @throws Exception
     */
    Object rawGet(int row, int column) throws Exception;

    /**
     * A helper method that validates the specified row and column and retrieves the value provided by a supplier.
     * @param row the row index (starting with 0)
     * @param column the column index (starting with 1)
     * @param supplier a supplier that may throw an exception if it is not able to provide a result
     * @return the item provided by the supplier
     */
    default <T> T get(int row, int column, SupplierWithException<T> supplier) {
        if((row < 0) || (row >= getRowCount())) {
            throw new ViewrekaException(getName() + ": Index out of bounds while retrieving item at (col: " + column + ", row: " + row + "). rowCount = " + getRowCount());
        }
        if((column <= 0) || (column > getColumnCount())) {
            throw new ViewrekaException(getName() + ": Column out of bounds while retrieving item at (col: " + column + ", row: " + row + "). columnCount = " + getColumnCount());
        }
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new ViewrekaException(getName() + ": Cannot retrieve item at (col: " + column + ", row: " + row + ")", e);
        }
    }

    /**
     * Validates the specified row and column and retrieves the item found at the specified row and column.
     * @param row the row index (starting with 0)
     * @param column the column index (starting with 1)
     * @param type the expected type of the item to be retrieved
     * @return the item at the specified row and column
     * @throws ViewrekaException
     */
    default <T> T getValue(int row, int column, Class<T> type) throws ViewrekaException {
        return get(row, column, () -> rawGet(row, column, type));
    }
    /**
     * Validates the specified row and column and retrieves the item found at the specified row and column.
     * @param row the row index (starting with 0)
     * @param columnName the column name
     * @param type the expected type of the item to be retrieved
     * @return the item at the specified row and column
     * @throws ViewrekaException
     */
    default <T> T getValue(int row, String columnName, Class<T> type) throws ViewrekaException {
        return getValue(row, getColumn(columnName), type);
    }

    /**
     * Validates the specified row and column and retrieves the object found at the specified row and column.
     * @param row the row index (starting with 0)
     * @param column the column index (starting with 1)
     * @return the object at the specified row and column
     * @throws ViewrekaException
     */
    default Object getObject(int row, int column) throws ViewrekaException {
        return get(row, column, () -> rawGet(row, column));
    }

    /**
     * Validates the specified row and column and retrieves the bject found at the specified row and column.
     * @param row the row index (starting with 0)
     * @param columnName the column name
     * @return the object at the specified row and column
     * @throws ViewrekaException
     */
    default Object getObject(int row, String columnName) throws ViewrekaException {
        return getObject(row, getColumn(columnName));
    }


    /**
     * Validates the specified row and column and retrieves the string found at the specified row and column.
     * @param row the row index (starting with 0)
     * @param column the column index (starting with 1)
     * @return the string at the specified row and column
     * @throws ViewrekaException
     */
    default String getString(int row, int column) throws ViewrekaException {
        return getValue(row, column, String.class);
    }

    /**
     * Validates the specified row and column and retrieves the string found at the specified row and column.
     * @param row the row index (starting with 0)
     * @param columnName the column name
     * @return the string at the specified row and column
     * @throws ViewrekaException
     */
    default String getString(int row, String columnName) throws ViewrekaException {
        return getValue(row, columnName, String.class);
    }

    /**
     * Provides textual information about this dataset.
     * @return the info string
     */
    default String getInfo() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(getName()).append(':');
        sb.append(getRowCount()).append(" elements of type (");
        String sep = "";
        int columnCount = getColumnCount();
        for(int column = 1; column <= columnCount; column++) {
            sb.append(sep).append(getColumnName(column)) ;
            sep = ", ";
        }
        sb.append("), parameters: ").append(getParameterNames());
        return sb.toString();
    }

}
