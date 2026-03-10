package com.legacy.ordermanagement.services;

import com.legacy.ordermanagement.data.ProductRepository;
import com.legacy.ordermanagement.models.Product;

import java.util.Date;
import java.util.List;

/**
 * ProductService — minimal but still has anti-patterns:
 * manual dependency creation, no validation, synchronous.
 */
public class ProductService {

    private ProductRepository productRepository = new ProductRepository();

    public List<Product> getAllProducts() {
        System.out.println("Fetching all products...");
        return productRepository.getAllProducts();
    }

    public Product getProductById(Long id) {
        if (id == null) {
            throw new RuntimeException("Product ID cannot be null");
        }
        return productRepository.getProductById(id);
    }

    public List<Product> searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return productRepository.getAllProducts();
        }
        return productRepository.searchProducts(searchTerm);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.getProductsByCategory(category);
    }

    public Product createProduct(Product product) {
        // Minimal validation — no proper Bean Validation
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }
        if (product.getPrice() < 0) {
            throw new RuntimeException("Product price cannot be negative");
        }
        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            throw new RuntimeException("SKU is required");
        }

        product.setCreatedDate(new Date());
        product.setUpdatedDate(new Date());
        product.setActive(true);

        return productRepository.createProduct(product);
    }

    public void updateStock(Long productId, int quantityChange) {
        Product product = productRepository.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found: " + productId);
        }
        if (product.getStockQuantity() + quantityChange < 0) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        productRepository.updateStock(productId, quantityChange);
    }
}
