package database;

import java.sql.ResultSet;
import java.util.Map;

/**
 * Interface defining database operations - Demonstrates Interface usage
 * This interface must be implemented by any database class
 */
public interface DatabaseOperations {

    /**
     * Establishes a connection to the database
     * 
     * @return true if connection successful, false otherwise
     */
    boolean connect();

    /**
     * Closes the database connection
     */
    void closeConnection();

    /**
     * Inserts data into a specified table
     * 
     * @param table The table name
     * @param data  Map of column names and values
     * @return true if insert successful, false otherwise
     */
    boolean insertData(String table, Map<String, Object> data);

    /**
     * Fetches data from the database using a query
     * 
     * @param query The SQL query to execute
     * @return ResultSet containing the query results
     */
    ResultSet fetchData(String query);

    /**
     * Updates data in the database
     * 
     * @param table     The table name
     * @param data      Map of column names and new values
     * @param condition WHERE clause condition
     * @return true if update successful, false otherwise
     */
    boolean updateData(String table, Map<String, Object> data, String condition);

    /**
     * Deletes data from the database
     * 
     * @param table     The table name
     * @param condition WHERE clause condition
     * @return true if delete successful, false otherwise
     */
    boolean deleteData(String table, String condition);

    /**
     * Executes a custom SQL query (INSERT, UPDATE, DELETE)
     * 
     * @param query The SQL query to execute
     * @return true if execution successful, false otherwise
     */
    boolean executeQuery(String query);

    /**
     * Executes a custom SQL query with parameters (prevents SQL injection)
     * 
     * @param query  The SQL query with placeholders
     * @param params Array of parameters to bind to the query
     * @return true if execution successful, false otherwise
     */
    boolean executePreparedQuery(String query, Object[] params);

    /**
     * Checks if the database connection is active
     * 
     * @return true if connected, false otherwise
     */
    boolean isConnected();
}
