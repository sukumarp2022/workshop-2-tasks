package com.legacy.ordermanagement.controllers;

import com.google.gson.Gson;
import com.legacy.ordermanagement.models.Order;
import com.legacy.ordermanagement.models.OrderItem;
import com.legacy.ordermanagement.services.OrderService;
import com.legacy.ordermanagement.utils.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Legacy Order Controller — multiple anti-patterns:
 * - Manual JSON handling with Gson instead of Jackson auto-serialization
 * - No @RestController, uses @Controller + @ResponseBody
 * - No proper error handling (generic try-catch)
 * - No input validation
 * - Business logic in controller
 * - Hardcoded response strings
 * - Uses raw Map for responses instead of DTOs
 * - No pagination
 * - No API versioning
 * - No swagger/OpenAPI
 */
@Controller
@RequestMapping("/api/orders")
public class OrderController {

    // Manual instantiation — no @Autowired / constructor injection
    private OrderService orderService = new OrderService();
    private Gson gson = new Gson();

    /**
     * Get all orders — no pagination, returns everything.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            String json = gson.toJson(orders);
            return new ResponseEntity<String>(json, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", "Failed to fetch orders");
            error.put("message", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get order by ID.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getOrderById(@PathVariable("id") Long id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order == null) {
                Map<String, String> error = new HashMap<String, String>();
                error.put("error", "Order not found");
                return new ResponseEntity<String>(gson.toJson(error), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<String>(gson.toJson(order), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get orders by status — no input validation on status value.
     */
    @RequestMapping(value = "/status/{status}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getOrdersByStatus(@PathVariable("status") String status) {
        try {
            List<Order> orders = orderService.getOrdersByStatus(status);
            return new ResponseEntity<String>(gson.toJson(orders), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Place a new order — accepts raw Map, manual parsing.
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> placeOrder(@RequestBody String body) {
        try {
            // Manual JSON parsing instead of auto-deserialization
            Map<String, Object> request = gson.fromJson(body, Map.class);

            Long customerId = ((Double) request.get("customerId")).longValue();
            String paymentMethod = (String) request.get("paymentMethod");
            String shippingAddress = (String) request.get("shippingAddress");

            // Parse items manually
            List<Map<String, Object>> itemMaps = (List<Map<String, Object>>) request.get("items");
            List<OrderItem> items = new ArrayList<OrderItem>();
            for (Map<String, Object> itemMap : itemMaps) {
                OrderItem item = new OrderItem();
                item.setProductId(((Double) itemMap.get("productId")).longValue());
                item.setQuantity(((Double) itemMap.get("quantity")).intValue());
                if (itemMap.containsKey("discount")) {
                    item.setDiscount((Double) itemMap.get("discount"));
                }
                items.add(item);
            }

            Order order = orderService.placeOrder(customerId, items, paymentMethod, shippingAddress);

            Map<String, Object> response = new HashMap<String, Object>();
            response.put("message", "Order placed successfully");
            response.put("orderId", order.getId());
            response.put("totalAmount", order.getTotalAmount());

            return new ResponseEntity<String>(gson.toJson(response), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", "Failed to place order");
            error.put("message", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Update order status — no validation of allowed transitions.
     */
    @RequestMapping(value = "/{id}/status", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateOrderStatus(@PathVariable("id") Long id, @RequestBody String body) {
        try {
            Map<String, String> request = gson.fromJson(body, Map.class);
            String newStatus = request.get("status");

            orderService.updateOrderStatus(id, newStatus);

            Map<String, String> response = new HashMap<String, String>();
            response.put("message", "Order status updated");
            response.put("orderId", String.valueOf(id));
            response.put("newStatus", newStatus);

            return new ResponseEntity<String>(gson.toJson(response), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get order summary — returns plain text.
     */
    @RequestMapping(value = "/{id}/summary", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getOrderSummary(@PathVariable("id") Long id) {
        try {
            String summary = orderService.getOrderSummary(id);
            return new ResponseEntity<String>(summary, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get sales report — dates parsed from strings manually.
     */
    @RequestMapping(value = "/report", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getSalesReport(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        try {
            Date start = DateUtils.parseDate(startDate);
            Date end = DateUtils.parseDate(endDate);

            if (start == null || end == null) {
                Map<String, String> error = new HashMap<String, String>();
                error.put("error", "Invalid date format. Use yyyy-MM-dd");
                return new ResponseEntity<String>(gson.toJson(error), HttpStatus.BAD_REQUEST);
            }

            Map<String, Object> report = orderService.generateSalesReport(start, end);
            return new ResponseEntity<String>(gson.toJson(report), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete order — hard delete with no confirmation.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteOrder(@PathVariable("id") Long id) {
        try {
            orderService.deleteOrder(id);
            Map<String, String> response = new HashMap<String, String>();
            response.put("message", "Order deleted");
            return new ResponseEntity<String>(gson.toJson(response), HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
