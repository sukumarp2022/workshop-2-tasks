package com.legacy.ordermanagement.controllers;

import com.google.gson.Gson;
import com.legacy.ordermanagement.models.Product;
import com.legacy.ordermanagement.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Legacy Product Controller — same anti-patterns as OrderController.
 */
@Controller
@RequestMapping("/api/products")
public class ProductController {

    private ProductService productService = new ProductService();
    private Gson gson = new Gson();

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return new ResponseEntity<String>(gson.toJson(products), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getProductById(@PathVariable("id") Long id) {
        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                Map<String, String> error = new HashMap<String, String>();
                error.put("error", "Product not found");
                return new ResponseEntity<String>(gson.toJson(error), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<String>(gson.toJson(product), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> searchProducts(@RequestParam("q") String searchTerm) {
        try {
            List<Product> products = productService.searchProducts(searchTerm);
            return new ResponseEntity<String>(gson.toJson(products), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/category/{category}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getByCategory(@PathVariable("category") String category) {
        try {
            List<Product> products = productService.getProductsByCategory(category);
            return new ResponseEntity<String>(gson.toJson(products), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> createProduct(@RequestBody String body) {
        try {
            Product product = gson.fromJson(body, Product.class);
            Product created = productService.createProduct(product);

            Map<String, Object> response = new HashMap<String, Object>();
            response.put("message", "Product created successfully");
            response.put("productId", created.getId());

            return new ResponseEntity<String>(gson.toJson(response), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{id}/stock", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateStock(@PathVariable("id") Long id, @RequestBody String body) {
        try {
            Map<String, Object> request = gson.fromJson(body, Map.class);
            int quantityChange = ((Double) request.get("quantityChange")).intValue();

            productService.updateStock(id, quantityChange);

            Map<String, String> response = new HashMap<String, String>();
            response.put("message", "Stock updated");
            return new ResponseEntity<String>(gson.toJson(response), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<String, String>();
            error.put("error", e.getMessage());
            return new ResponseEntity<String>(gson.toJson(error), HttpStatus.BAD_REQUEST);
        }
    }
}
