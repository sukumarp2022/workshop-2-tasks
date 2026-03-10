package com.legacy.ordermanagement.models;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Order model — uses old java.util.Date, mutable fields, no encapsulation.
 * Missing equals/hashCode, toString, validation.
 */
public class Order {

    public Long id;
    public Long customerId;
    public String customerName;
    public String customerEmail;
    public String shippingAddress;
    public String status; // "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"
    public Date orderDate;
    public Date shippedDate;
    public Date deliveredDate;
    public double totalAmount;
    public double taxAmount;
    public double shippingCost;
    public String paymentMethod; // "CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "CASH_ON_DELIVERY"
    public String paymentStatus; // "PENDING", "COMPLETED", "FAILED", "REFUNDED"
    public String notes;
    public List<OrderItem> items = new ArrayList<OrderItem>();

    public Order() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public Date getShippedDate() { return shippedDate; }
    public void setShippedDate(Date shippedDate) { this.shippedDate = shippedDate; }

    public Date getDeliveredDate() { return deliveredDate; }
    public void setDeliveredDate(Date deliveredDate) { this.deliveredDate = deliveredDate; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getShippingCost() { return shippingCost; }
    public void setShippingCost(double shippingCost) { this.shippingCost = shippingCost; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
