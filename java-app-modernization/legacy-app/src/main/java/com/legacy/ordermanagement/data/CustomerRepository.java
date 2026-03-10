package com.legacy.ordermanagement.data;

import com.legacy.ordermanagement.models.Customer;
import com.legacy.ordermanagement.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Legacy Customer Repository — raw JDBC, SQL injection risks.
 */
public class CustomerRepository {

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<Customer>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM customers WHERE is_active = TRUE ORDER BY last_name");

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return customers;
    }

    public Customer getCustomerById(Long id) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM customers WHERE id = " + id);

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return null;
    }

    public Customer getCustomerByEmail(String email) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            // SQL INJECTION VULNERABILITY
            rs = stmt.executeQuery("SELECT * FROM customers WHERE email = '" + email + "'");

            if (rs.next()) {
                return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return null;
    }

    public Customer createCustomer(Customer customer) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO customers (first_name, last_name, email, phone, " +
                "address, city, state, zip_code, country, registered_date, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getAddress());
            pstmt.setString(6, customer.getCity());
            pstmt.setString(7, customer.getState());
            pstmt.setString(8, customer.getZipCode());
            pstmt.setString(9, customer.getCountry());
            pstmt.setTimestamp(10, new Timestamp(new java.util.Date().getTime()));
            pstmt.setBoolean(11, true);

            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                customer.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create customer", e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return customer;
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        customer.setCity(rs.getString("city"));
        customer.setState(rs.getString("state"));
        customer.setZipCode(rs.getString("zip_code"));
        customer.setCountry(rs.getString("country"));
        customer.setRegisteredDate(rs.getTimestamp("registered_date"));
        customer.setActive(rs.getBoolean("is_active"));
        return customer;
    }
}
