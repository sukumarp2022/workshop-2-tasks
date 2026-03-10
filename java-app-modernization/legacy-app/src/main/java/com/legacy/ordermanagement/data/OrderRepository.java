package com.legacy.ordermanagement.data;

import com.legacy.ordermanagement.models.Order;
import com.legacy.ordermanagement.models.OrderItem;
import com.legacy.ordermanagement.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Legacy Order Repository — raw JDBC, no ORM, manual ResultSet mapping,
 * SQL string concatenation (SQL injection risk), no proper resource management.
 */
public class OrderRepository {

    /**
     * Gets all orders — no pagination, loads everything into memory.
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<Order>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM orders ORDER BY order_date DESC");

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Bad: swallows exception
        } finally {
            // Manual resource cleanup — not using try-with-resources
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
            // Note: connection NOT closed — reused from DatabaseUtil singleton
        }

        return orders;
    }

    /**
     * Gets order by ID — uses string concatenation for SQL (SQL injection risk).
     */
    public Order getOrderById(Long id) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            // SQL INJECTION VULNERABILITY — string concatenation
            rs = stmt.executeQuery("SELECT * FROM orders WHERE id = " + id);

            if (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(getOrderItems(order.getId()));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return null; // Returns null instead of Optional
    }

    /**
     * Gets orders by customer — string concatenation.
     */
    public List<Order> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = new ArrayList<Order>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM orders WHERE customer_id = " + customerId);

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                order.setItems(getOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return orders;
    }

    /**
     * Gets orders by status — string concatenation with user-supplied value.
     */
    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<Order>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            // SQL INJECTION VULNERABILITY — string concatenation with user input
            rs = stmt.executeQuery("SELECT * FROM orders WHERE status = '" + status + "'");

            while (rs.next()) {
                Order order = mapResultSetToOrder(rs);
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return orders;
    }

    /**
     * Creates an order — manual SQL building, no transaction management.
     */
    public Order createOrder(Order order) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();

            String sql = "INSERT INTO orders (customer_id, customer_name, customer_email, " +
                "shipping_address, status, order_date, total_amount, tax_amount, " +
                "shipping_cost, payment_method, payment_status, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setLong(1, order.getCustomerId());
            pstmt.setString(2, order.getCustomerName());
            pstmt.setString(3, order.getCustomerEmail());
            pstmt.setString(4, order.getShippingAddress());
            pstmt.setString(5, "PENDING"); // Hardcoded initial status
            pstmt.setTimestamp(6, new Timestamp(new java.util.Date().getTime()));
            pstmt.setDouble(7, order.getTotalAmount());
            pstmt.setDouble(8, order.getTaxAmount());
            pstmt.setDouble(9, order.getShippingCost());
            pstmt.setString(10, order.getPaymentMethod());
            pstmt.setString(11, "PENDING"); // Hardcoded
            pstmt.setString(12, order.getNotes());

            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                order.setId(rs.getLong(1));
            }

            // Insert order items — no batch processing
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    insertOrderItem(order.getId(), item);
                }
            }

            System.out.println("Order created with ID: " + order.getId());

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create order", e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return order;
    }

    /**
     * Updates order status — no state machine validation.
     */
    public void updateOrderStatus(Long orderId, String newStatus) {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            // SQL INJECTION — string concatenation
            stmt.executeUpdate("UPDATE orders SET status = '" + newStatus + "' WHERE id = " + orderId);

            // If shipped, set shipped date
            if ("SHIPPED".equals(newStatus)) {
                stmt.executeUpdate("UPDATE orders SET shipped_date = CURRENT_TIMESTAMP WHERE id = " + orderId);
            }
            if ("DELIVERED".equals(newStatus)) {
                stmt.executeUpdate("UPDATE orders SET delivered_date = CURRENT_TIMESTAMP WHERE id = " + orderId);
            }

            System.out.println("Order " + orderId + " status updated to " + newStatus);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    /**
     * Deletes an order — hard delete, no soft delete or audit trail.
     */
    public void deleteOrder(Long orderId) {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM order_items WHERE order_id = " + orderId);
            stmt.executeUpdate("DELETE FROM orders WHERE id = " + orderId);
            System.out.println("Order " + orderId + " deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    // --- Private helpers ---

    private void insertOrderItem(Long orderId, OrderItem item) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO order_items (order_id, product_id, product_name, " +
                "quantity, unit_price, discount, line_total) VALUES (?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, orderId);
            pstmt.setLong(2, item.getProductId());
            pstmt.setString(3, item.getProductName());
            pstmt.setInt(4, item.getQuantity());
            pstmt.setDouble(5, item.getUnitPrice());
            pstmt.setDouble(6, item.getDiscount());
            pstmt.setDouble(7, item.getQuantity() * item.getUnitPrice() * (1 - item.getDiscount() / 100));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    private List<OrderItem> getOrderItems(Long orderId) {
        List<OrderItem> items = new ArrayList<OrderItem>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM order_items WHERE order_id = " + orderId);

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getLong("id"));
                item.setOrderId(rs.getLong("order_id"));
                item.setProductId(rs.getLong("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPrice(rs.getDouble("unit_price"));
                item.setDiscount(rs.getDouble("discount"));
                item.setLineTotal(rs.getDouble("line_total"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return items;
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setCustomerId(rs.getLong("customer_id"));
        order.setCustomerName(rs.getString("customer_name"));
        order.setCustomerEmail(rs.getString("customer_email"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setStatus(rs.getString("status"));
        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setShippedDate(rs.getTimestamp("shipped_date"));
        order.setDeliveredDate(rs.getTimestamp("delivered_date"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setTaxAmount(rs.getDouble("tax_amount"));
        order.setShippingCost(rs.getDouble("shipping_cost"));
        order.setPaymentMethod(rs.getString("payment_method"));
        order.setPaymentStatus(rs.getString("payment_status"));
        order.setNotes(rs.getString("notes"));
        return order;
    }
}
