package com.legacy.ordermanagement;

import com.legacy.ordermanagement.models.Order;
import com.legacy.ordermanagement.models.OrderItem;
import com.legacy.ordermanagement.services.OrderService;
import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Legacy test — JUnit 4, minimal coverage, no mocking.
 */
public class OrderServiceTest {

    private OrderService orderService;

    @Before
    public void setUp() {
        orderService = new OrderService();
        // Note: depends on real database — not a true unit test
    }

    @Test
    public void testGetAllOrdersReturnsNotNull() {
        // Weak test — only checks for not null
        List<Order> orders = orderService.getAllOrders();
        assertNotNull(orders);
    }

    @Test
    public void testGetOrderSummaryForMissingOrder() {
        String summary = orderService.getOrderSummary(999L);
        assertEquals("Order not found", summary);
    }
}
