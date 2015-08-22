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

/**
 * A dataset based on a {@link ResultSet}.
 */
public class SqlDataset implements Dataset {
    private static final Logger log = LoggerFactory.getLogger(SqlDataset.class);

    private final String name;
    private final ResultSet resultSet;
    private final PreparedStatement statement;
    private final Set<String> parameterNames;
    private final int rowCount;
    private final int columnCount;
    private final Map<String, Integer> columns = new LinkedHashMap<>();

    private final Object rsLock = new Object();

    /**
     * Constructs an SQL dataset.
     * @param name the name of the dataset
     * @param resultSet the result set that backs this dataset
     * @param parameterNames the names of the parameters of the corresponding {@link PreparedStatement}
     * @param statement the {@link PreparedStatement} that has produced the result set backing this dataset
     */
    public SqlDataset(String name, ResultSet resultSet, Set<String> parameterNames, PreparedStatement statement) {
        this.name = name;
        this.resultSet = resultSet;
        this.statement = statement;
        this.parameterNames = parameterNames;
        try {
            resultSet.beforeFirst();
            boolean valid = resultSet.last();
            rowCount = valid ? resultSet.getRow() : 0;
            ResultSetMetaData metaData = resultSet.getMetaData();
            columnCount = metaData.getColumnCount();
            for(int i=1; i<=columnCount; i++) {
                columns.put(metaData.getColumnName(i).toUpperCase(), i);
            }
            log.debug("SqlDataset {} with {} rows and {} columns created.", name, rowCount, columnCount);
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
    public boolean isClosed() {
        try {
            return statement.isClosed();
        } catch(SQLException e) {
            return true;
        }
    }

    @Override
    public Set<String> getParameterNames() {
        return parameterNames;
    }

    @Override
    public int getRowCount() {
        return rowCount;
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
    public <T> T rawGet(int row, int column, Class<T> type) throws Exception {
        try {
            synchronized(rsLock) {
                moveTo(row);
                return resultSet.getObject(column, type);
            }
        } catch (SQLException e) {
            throw new ViewrekaException("rawGet(" + column + ", " + row + ", " + type + ") failed for dataset " + name, e);
        }
    }

    /**
     * Retrieves the item found at a specified row and column, without checking the validity of the specified row and column.
     * @param row the row index (starting with 0)
     * @param column the column name
     * @param type the expected type of the item to be retrieved
     * @return the item at the specified row and column
     * @throws Exception
     */
    public <T> T rawGet(int row, String column, Class<T> type) throws Exception {
        try {
            synchronized(rsLock) {
                moveTo(row);
                return resultSet.getObject(column, type);
            }
        } catch (SQLException e) {
            throw new ViewrekaException("rawGet(" + column + ", " + row + ", " + type + ") failed for dataset " + name, e);
        }
    }

    @Override
    public Object rawGet(int row, int column) throws Exception {
        try {
            synchronized(rsLock) {
                moveTo(row);
                return resultSet.getObject(column);
            }
        } catch (SQLException e) {
            throw new ViewrekaException("rawGet(" + column + ", " + row + ") failed for dataset " + name, e);
        }
    }

    /**
     * Retrieves the object found at a specified row and column, without checking the validity of the specified row and column.
     * @param row the row index (starting with 0)
     * @param column the column name
     * @return the object found at the specified row and column.
     * @throws Exception
     */
    public Object rawGet(int row, String column) throws Exception {
        try {
            synchronized(rsLock) {
                moveTo(row);
                return resultSet.getObject(column);
            }
        } catch (SQLException e) {
            throw new ViewrekaException("rawGet(" + column + ", " + row + ") failed for dataset " + name, e);
        }
    }


    /**
     * Validates the specified row and column and retrieves the item provided by the specified supplier.
     * @param row the row index (starting with 0)
     * @param columnName the column name
     * @param supplier a supplier that may throw an exception
     * @return the item at the specified row and column
     * @throws ViewrekaException if the validation failed or of the supplier thrown an exception
     */
    <T> T get(int row, String columnName, SupplierWithException<T> supplier) {
        if((row < 0) || (row >= getRowCount())) {
            throw new ViewrekaException(getName() + ": Index out of bounds while retrieving item at (col: " + columnName + ", row: " + row + "). rowCount = " + getRowCount());
        }
        if(columnName == null) {
            throw new ViewrekaException(getName() + ": Null column name while retrieving item at row: " + row + ".");
        }
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new ViewrekaException(getName() + ": Cannot retrieve item at (col: " + columnName + ", idx: " + row + ")", e);
        }
    }


    @Override
    public <T> T getValue(int row, String columnName, Class<T> type) throws ViewrekaException {
        return get(row, columnName, () -> rawGet(row, columnName, type));
    }

    @Override
    public Object getObject(int row, String columnName) throws ViewrekaException {
        return get(row, columnName, () -> rawGet(row, columnName));
    }

    /**
     * Moves the cursor of the result set to the specified row
     * @param row the row to which the cursor of the result set should be moved
     * @throws Exception if the move failed
     */
    private void moveTo(int row) throws Exception {
        synchronized(rsLock) {
            int currIndex = resultSet.getRow();
            if(currIndex <= 0) {
                if(!resultSet.first()) {
                    throw new SQLException("Trying to move to row " + row + " of an empty resultSet.");
                }
                currIndex = 1;
            }
            int offset = (row + 1) - currIndex;
            boolean ok = resultSet.relative(offset);
            if(!ok) {
                throw new SQLException("Cannot move to row " + row);
            }
        }
    }

    @Override
    public String toString() {
        return getInfo();
    }
}
