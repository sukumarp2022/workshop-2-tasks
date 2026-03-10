package com.legacy.ordermanagement.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Legacy database utility — manual connection management, hardcoded credentials,
 * no connection pooling, static methods, no proper resource management.
 */
public class DatabaseUtil {

    // Hardcoded database configuration
    private static final String DB_URL = "jdbc:h2:mem:orderdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "org.h2.Driver";

    private static Connection connection = null;

    // Static initializer block — loads driver class the old way
    static {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // Bad practice: swallows exception
        }
    }

    /**
     * Returns a singleton connection — not thread-safe, no pooling.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    /**
     * Closes connection — silently swallows exceptions.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Silently swallowed
            }
        }
    }

    /**
     * Initializes the database schema — inline SQL, no migration tool.
     */
    public static void initializeDatabase() {
        Connection conn = null;
        try {
            conn = getConnection();

            // Creating tables with inline SQL — no Flyway/Liquibase
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS customers (" +
                "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "  first_name VARCHAR(100)," +
                "  last_name VARCHAR(100)," +
                "  email VARCHAR(200)," +
                "  phone VARCHAR(20)," +
                "  address VARCHAR(500)," +
                "  city VARCHAR(100)," +
                "  state VARCHAR(50)," +
                "  zip_code VARCHAR(10)," +
                "  country VARCHAR(50)," +
                "  registered_date TIMESTAMP," +
                "  is_active BOOLEAN DEFAULT TRUE" +
                ")"
            );

            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS products (" +
                "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "  name VARCHAR(200)," +
                "  description VARCHAR(1000)," +
                "  category VARCHAR(100)," +
                "  price DOUBLE," +
                "  stock_quantity INT," +
                "  sku VARCHAR(50)," +
                "  is_active BOOLEAN DEFAULT TRUE," +
                "  created_date TIMESTAMP," +
                "  updated_date TIMESTAMP" +
                ")"
            );

            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS orders (" +
                "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "  customer_id BIGINT," +
                "  customer_name VARCHAR(200)," +
                "  customer_email VARCHAR(200)," +
                "  shipping_address VARCHAR(500)," +
                "  status VARCHAR(20)," +
                "  order_date TIMESTAMP," +
                "  shipped_date TIMESTAMP," +
                "  delivered_date TIMESTAMP," +
                "  total_amount DOUBLE," +
                "  tax_amount DOUBLE," +
                "  shipping_cost DOUBLE," +
                "  payment_method VARCHAR(30)," +
                "  payment_status VARCHAR(20)," +
                "  notes VARCHAR(1000)" +
                ")"
            );

            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS order_items (" +
                "  id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "  order_id BIGINT," +
                "  product_id BIGINT," +
                "  product_name VARCHAR(200)," +
                "  quantity INT," +
                "  unit_price DOUBLE," +
                "  discount DOUBLE," +
                "  line_total DOUBLE" +
                ")"
            );

            // Insert sample data
            conn.createStatement().execute(
                "INSERT INTO customers (first_name, last_name, email, phone, address, city, state, zip_code, country, registered_date, is_active) VALUES " +
                "('John', 'Doe', 'john.doe@email.com', '555-0101', '123 Main St', 'Springfield', 'IL', '62701', 'US', CURRENT_TIMESTAMP, TRUE)," +
                "('Jane', 'Smith', 'jane.smith@email.com', '555-0102', '456 Oak Ave', 'Portland', 'OR', '97201', 'US', CURRENT_TIMESTAMP, TRUE)," +
                "('Bob', 'Wilson', 'bob.wilson@email.com', '555-0103', '789 Pine Rd', 'Austin', 'TX', '73301', 'US', CURRENT_TIMESTAMP, TRUE)"
            );

            conn.createStatement().execute(
                "INSERT INTO products (name, description, category, price, stock_quantity, sku, is_active, created_date, updated_date) VALUES " +
                "('Wireless Mouse', 'Ergonomic wireless mouse with USB receiver', 'Electronics', 29.99, 150, 'ELEC-001', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)," +
                "('Mechanical Keyboard', 'RGB mechanical keyboard with Cherry MX switches', 'Electronics', 89.99, 75, 'ELEC-002', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)," +
                "('USB-C Hub', '7-in-1 USB-C hub with HDMI and ethernet', 'Electronics', 49.99, 200, 'ELEC-003', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)," +
                "('Monitor Stand', 'Adjustable aluminum monitor stand', 'Accessories', 39.99, 100, 'ACC-001', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)," +
                "('Desk Lamp', 'LED desk lamp with adjustable brightness', 'Accessories', 24.99, 120, 'ACC-002', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
            );

            System.out.println("Database initialized successfully."); // Using System.out instead of logger

        } catch (SQLException e) {
            e.printStackTrace(); // Bad practice
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}
