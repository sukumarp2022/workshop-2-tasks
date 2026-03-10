package com.legacy.ordermanagement.data;

import com.legacy.ordermanagement.models.Product;
import com.legacy.ordermanagement.utils.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Legacy Product Repository — raw JDBC, SQL injection risks, no pagination.
 */
public class ProductRepository {

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<Product>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM products WHERE is_active = TRUE ORDER BY name");

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return products;
    }

    public Product getProductById(Long id) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM products WHERE id = " + id);

            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return null;
    }

    /**
     * Search products — SQL injection vulnerability with user-supplied search term.
     */
    public List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<Product>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            // SQL INJECTION VULNERABILITY
            rs = stmt.executeQuery(
                "SELECT * FROM products WHERE name LIKE '%" + searchTerm + "%' " +
                "OR description LIKE '%" + searchTerm + "%' " +
                "OR category LIKE '%" + searchTerm + "%'"
            );

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return products;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<Product>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM products WHERE category = '" + category + "' AND is_active = TRUE");

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return products;
    }

    public Product createProduct(Product product) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            String sql = "INSERT INTO products (name, description, category, price, " +
                "stock_quantity, sku, is_active, created_date, updated_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setString(3, product.getCategory());
            pstmt.setDouble(4, product.getPrice());
            pstmt.setInt(5, product.getStockQuantity());
            pstmt.setString(6, product.getSku());
            pstmt.setBoolean(7, true);
            pstmt.setTimestamp(8, new Timestamp(new java.util.Date().getTime()));
            pstmt.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));

            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                product.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create product", e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignored */ }
        }

        return product;
    }

    public void updateStock(Long productId, int quantityChange) {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            // No concurrency control, race condition possible
            stmt.executeUpdate(
                "UPDATE products SET stock_quantity = stock_quantity + (" + quantityChange + "), " +
                "updated_date = CURRENT_TIMESTAMP WHERE id = " + productId
            );
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setCategory(rs.getString("category"));
        product.setPrice(rs.getDouble("price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setSku(rs.getString("sku"));
        product.setActive(rs.getBoolean("is_active"));
        product.setCreatedDate(rs.getTimestamp("created_date"));
        product.setUpdatedDate(rs.getTimestamp("updated_date"));
        return product;
    }
}
