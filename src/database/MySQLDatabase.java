package database;

import java.sql.*;
import java.util.Map;

/**
 * MySQL Database implementation - Implements DatabaseOperations interface
 * Demonstrates Interface implementation and JDBC connectivity
 */
public class MySQLDatabase implements DatabaseOperations {

    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/scms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root"; // Change as needed
    private static final String DB_PASSWORD = ""; // Change as needed

    private Connection connection;
    private static MySQLDatabase instance; // Singleton pattern

    // Private constructor for singleton pattern
    private MySQLDatabase() {
    }

    /**
     * Get singleton instance of MySQLDatabase
     */
    public static MySQLDatabase getInstance() {
        if (instance == null) {
            instance = new MySQLDatabase();
        }
        return instance;
    }

    @Override
    public boolean connect() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully!");
            return true;

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            System.err.println("Failed to connect to database!");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection!");
            e.printStackTrace();
        }
    }

    @Override
    public boolean insertData(String table, Map<String, Object> data) {
        if (!isConnected()) {
            System.err.println("No database connection!");
            return false;
        }

        try {
            // Build INSERT query
            StringBuilder columns = new StringBuilder();
            StringBuilder values = new StringBuilder();

            for (String column : data.keySet()) {
                if (columns.length() > 0) {
                    columns.append(", ");
                    values.append(", ");
                }
                columns.append(column);
                values.append("?");
            }

            String query = "INSERT INTO " + table + " (" + columns + ") VALUES (" + values + ")";

            // Execute with prepared statement
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                int index = 1;
                for (Object value : data.values()) {
                    stmt.setObject(index++, value);
                }

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error inserting data into " + table);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResultSet fetchData(String query) {
        if (!isConnected()) {
            System.err.println("No database connection!");
            return null;
        }

        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(query);

        } catch (SQLException e) {
            System.err.println("Error fetching data!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean updateData(String table, Map<String, Object> data, String condition) {
        if (!isConnected()) {
            System.err.println("No database connection!");
            return false;
        }

        try {
            // Build UPDATE query
            StringBuilder setClause = new StringBuilder();

            for (String column : data.keySet()) {
                if (setClause.length() > 0) {
                    setClause.append(", ");
                }
                setClause.append(column).append(" = ?");
            }

            String query = "UPDATE " + table + " SET " + setClause + " WHERE " + condition;

            // Execute with prepared statement
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                int index = 1;
                for (Object value : data.values()) {
                    stmt.setObject(index++, value);
                }

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error updating data in " + table);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteData(String table, String condition) {
        if (!isConnected()) {
            System.err.println("No database connection!");
            return false;
        }

        try {
            String query = "DELETE FROM " + table + " WHERE " + condition;
            Statement stmt = connection.createStatement();
            int rowsAffected = stmt.executeUpdate(query);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting data from " + table);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean executeQuery(String query) {
        if (!isConnected()) {
            System.err.println("No database connection!");
            return false;
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(query);
            return true;

        } catch (SQLException e) {
            System.err.println("Error executing query!");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean executePreparedQuery(String query, Object[] params) {
        if (!isConnected()) {
            System.err.println("No database connection!");
            return false;
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Bind parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error executing prepared query!");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Get the active connection object
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Execute query and return generated keys
     */
    public int executeInsertAndGetId(String query, Object[] params) {
        if (!isConnected()) {
            System.err.println("No database connection!");
            return -1;
        }

        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Bind parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            stmt.executeUpdate();

            // Get generated key
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error executing insert query!");
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Execute a prepared query and return ResultSet
     */
    public ResultSet executePreparedSelect(String query, Object[] params) {
        if (!isConnected()) {
            System.err.println("No database connection!");
            return null;
        }

        try {
            PreparedStatement stmt = connection.prepareStatement(query);

            // Bind parameters
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            return stmt.executeQuery();

        } catch (SQLException e) {
            System.err.println("Error executing prepared select!");
            e.printStackTrace();
            return null;
        }
    }
}
