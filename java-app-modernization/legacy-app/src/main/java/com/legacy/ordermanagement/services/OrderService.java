package com.legacy.ordermanagement.services;

import com.legacy.ordermanagement.data.CustomerRepository;
import com.legacy.ordermanagement.data.OrderRepository;
import com.legacy.ordermanagement.data.ProductRepository;
import com.legacy.ordermanagement.models.Customer;
import com.legacy.ordermanagement.models.Order;
import com.legacy.ordermanagement.models.OrderItem;
import com.legacy.ordermanagement.models.Product;
import com.legacy.ordermanagement.utils.DateUtils;

import java.util.*;

/**
 * GOD CLASS — OrderService handles orders, inventory, pricing, notifications,
 * and reporting. Violates Single Responsibility Principle.
 *
 * Anti-patterns:
 * - Creates its own dependencies (no DI)
 * - Business logic mixed with infrastructure
 * - Hardcoded magic numbers
 * - No input validation
 * - Uses System.out for logging
 * - Synchronous everything
 * - No error handling strategy
 */
public class OrderService {

    // Creating dependencies manually — no dependency injection
    private OrderRepository orderRepository = new OrderRepository();
    private ProductRepository productRepository = new ProductRepository();
    private CustomerRepository customerRepository = new CustomerRepository();

    // Magic numbers — hardcoded business rules
    private static final double TAX_RATE = 0.08;       // 8% tax
    private static final double FREE_SHIPPING_THRESHOLD = 50.0;
    private static final double STANDARD_SHIPPING = 5.99;
    private static final double EXPRESS_SHIPPING = 12.99;
    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final double BULK_DISCOUNT_THRESHOLD = 100.0;
    private static final double BULK_DISCOUNT_PERCENT = 5.0;

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public Order getOrderById(Long id) {
        return orderRepository.getOrderById(id);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.getOrdersByStatus(status);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteOrder(id);
    }

    /**
     * Places an order — does EVERYTHING: validation, pricing, inventory, notifications.
     * This method is way too long and does too much.
     */
    public Order placeOrder(Long customerId, List<OrderItem> items, String paymentMethod, String shippingAddress) {

        // 1. Validate customer exists
        Customer customer = customerRepository.getCustomerById(customerId);
        if (customer == null) {
            throw new RuntimeException("Customer not found: " + customerId);
        }

        // 2. Validate and price each item
        double subtotal = 0;
        for (OrderItem item : items) {
            Product product = productRepository.getProductById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("Product not found: " + item.getProductId());
            }
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setLineTotal(product.getPrice() * item.getQuantity());

            // Apply discount if applicable
            if (item.getDiscount() > 0) {
                item.setLineTotal(item.getLineTotal() * (1 - item.getDiscount() / 100));
            }

            subtotal += item.getLineTotal();
        }

        // 3. Apply bulk discount — business rule embedded in service
        if (subtotal > BULK_DISCOUNT_THRESHOLD) {
            subtotal = subtotal * (1 - BULK_DISCOUNT_PERCENT / 100);
            System.out.println("Bulk discount applied: " + BULK_DISCOUNT_PERCENT + "%");
        }

        // 4. Calculate tax
        double tax = subtotal * TAX_RATE;

        // 5. Calculate shipping
        double shipping = subtotal >= FREE_SHIPPING_THRESHOLD ? 0 : STANDARD_SHIPPING;

        // 6. Build order
        Order order = new Order();
        order.setCustomerId(customerId);
        order.setCustomerName(customer.getFullName());
        order.setCustomerEmail(customer.getEmail());
        order.setShippingAddress(shippingAddress != null ? shippingAddress : customer.getAddress());
        order.setTotalAmount(subtotal + tax + shipping);
        order.setTaxAmount(tax);
        order.setShippingCost(shipping);
        order.setPaymentMethod(paymentMethod);
        order.setItems(items);
        order.setOrderDate(new Date()); // Uses java.util.Date

        // 7. Save order
        Order savedOrder = orderRepository.createOrder(order);

        // 8. Update inventory — no transaction boundary
        for (OrderItem item : items) {
            productRepository.updateStock(item.getProductId(), -item.getQuantity());

            // Check low stock — business logic leak
            Product product = productRepository.getProductById(item.getProductId());
            if (product.getStockQuantity() < LOW_STOCK_THRESHOLD) {
                sendLowStockAlert(product); // Side effect buried in order placement
            }
        }

        // 9. Send confirmation — synchronous, blocks the request
        sendOrderConfirmation(savedOrder);

        // 10. Log — using System.out
        System.out.println("Order placed: " + savedOrder.getId() +
            " | Total: $" + savedOrder.getTotalAmount() +
            " | Date: " + DateUtils.formatDateTime(savedOrder.getOrderDate()));

        return savedOrder;
    }

    /**
     * Updates order status — no state machine, allows invalid transitions.
     */
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found: " + orderId);
        }

        // No validation of state transitions — can go from DELIVERED to PENDING
        orderRepository.updateOrderStatus(orderId, newStatus);

        // Send notification based on status
        if ("SHIPPED".equals(newStatus)) {
            sendShippingNotification(order);
        } else if ("DELIVERED".equals(newStatus)) {
            sendDeliveryNotification(order);
        } else if ("CANCELLED".equals(newStatus)) {
            // Restore inventory — but no transaction!
            for (OrderItem item : order.getItems()) {
                productRepository.updateStock(item.getProductId(), item.getQuantity());
            }
            sendCancellationNotification(order);
        }
    }

    /**
     * Gets order summary — does formatting in service layer.
     */
    public String getOrderSummary(Long orderId) {
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            return "Order not found";
        }

        // Building display string in service — should be in view/DTO layer
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("          ORDER SUMMARY\n");
        sb.append("========================================\n");
        sb.append("Order #: ").append(order.getId()).append("\n");
        sb.append("Date: ").append(DateUtils.formatForDisplay(order.getOrderDate())).append("\n");
        sb.append("Customer: ").append(order.getCustomerName()).append("\n");
        sb.append("Email: ").append(order.getCustomerEmail()).append("\n");
        sb.append("Ship To: ").append(order.getShippingAddress()).append("\n");
        sb.append("Status: ").append(order.getStatus()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Items:\n");

        for (OrderItem item : order.getItems()) {
            sb.append("  - ").append(item.getProductName())
              .append(" x").append(item.getQuantity())
              .append(" @ $").append(String.format("%.2f", item.getUnitPrice()))
              .append(" = $").append(String.format("%.2f", item.getLineTotal()))
              .append("\n");
        }

        sb.append("----------------------------------------\n");
        sb.append("Subtotal: $").append(String.format("%.2f", order.getTotalAmount() - order.getTaxAmount() - order.getShippingCost())).append("\n");
        sb.append("Tax: $").append(String.format("%.2f", order.getTaxAmount())).append("\n");
        sb.append("Shipping: $").append(String.format("%.2f", order.getShippingCost())).append("\n");
        sb.append("TOTAL: $").append(String.format("%.2f", order.getTotalAmount())).append("\n");
        sb.append("========================================\n");
        sb.append("Payment: ").append(order.getPaymentMethod()).append(" (").append(order.getPaymentStatus()).append(")\n");

        return sb.toString();
    }

    /**
     * Generates sales report — reporting logic mixed with order service.
     */
    public Map<String, Object> generateSalesReport(Date startDate, Date endDate) {
        List<Order> allOrders = orderRepository.getAllOrders();
        Map<String, Object> report = new HashMap<String, Object>();

        double totalRevenue = 0;
        double totalTax = 0;
        int orderCount = 0;
        int cancelledCount = 0;
        Map<String, Integer> statusCounts = new HashMap<String, Integer>();
        Map<String, Double> categoryRevenue = new HashMap<String, Double>();

        for (Order order : allOrders) {
            // Manual date filtering — no database-level filtering
            if (order.getOrderDate() != null &&
                order.getOrderDate().after(DateUtils.getStartOfDay(startDate)) &&
                order.getOrderDate().before(DateUtils.getEndOfDay(endDate))) {

                orderCount++;
                totalRevenue += order.getTotalAmount();
                totalTax += order.getTaxAmount();

                if ("CANCELLED".equals(order.getStatus())) {
                    cancelledCount++;
                }

                // Count by status
                String status = order.getStatus();
                if (statusCounts.containsKey(status)) {
                    statusCounts.put(status, statusCounts.get(status) + 1);
                } else {
                    statusCounts.put(status, 1);
                }

                // Revenue by category — N+1 query problem
                for (OrderItem item : order.getItems()) {
                    Product product = productRepository.getProductById(item.getProductId());
                    if (product != null) {
                        String category = product.getCategory();
                        if (categoryRevenue.containsKey(category)) {
                            categoryRevenue.put(category, categoryRevenue.get(category) + item.getLineTotal());
                        } else {
                            categoryRevenue.put(category, item.getLineTotal());
                        }
                    }
                }
            }
        }

        report.put("totalRevenue", totalRevenue);
        report.put("totalTax", totalTax);
        report.put("orderCount", orderCount);
        report.put("cancelledCount", cancelledCount);
        report.put("averageOrderValue", orderCount > 0 ? totalRevenue / orderCount : 0);
        report.put("statusBreakdown", statusCounts);
        report.put("categoryRevenue", categoryRevenue);
        report.put("reportDate", DateUtils.formatDateTime(new Date()));
        report.put("period", DateUtils.formatDate(startDate) + " to " + DateUtils.formatDate(endDate));

        System.out.println("Sales report generated for " + report.get("period"));

        return report;
    }

    // --- Notification methods — should be a separate service ---

    private void sendOrderConfirmation(Order order) {
        // Simulated email sending — synchronous, no retry, no template
        System.out.println("SENDING EMAIL to " + order.getCustomerEmail());
        System.out.println("Subject: Order Confirmation #" + order.getId());
        System.out.println("Body: Thank you for your order! Total: $" + order.getTotalAmount());
        // In real code, this would call an SMTP server synchronously
    }

    private void sendShippingNotification(Order order) {
        System.out.println("SENDING EMAIL to " + order.getCustomerEmail());
        System.out.println("Subject: Your order #" + order.getId() + " has been shipped!");
    }

    private void sendDeliveryNotification(Order order) {
        System.out.println("SENDING EMAIL to " + order.getCustomerEmail());
        System.out.println("Subject: Your order #" + order.getId() + " has been delivered!");
    }

    private void sendCancellationNotification(Order order) {
        System.out.println("SENDING EMAIL to " + order.getCustomerEmail());
        System.out.println("Subject: Your order #" + order.getId() + " has been cancelled.");
    }

    private void sendLowStockAlert(Product product) {
        System.out.println("LOW STOCK ALERT: " + product.getName() +
            " (SKU: " + product.getSku() + ") — only " + product.getStockQuantity() + " remaining");
    }
}
