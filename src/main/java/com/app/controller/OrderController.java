package com.app.controller;

import com.app.model.*;
import com.app.repository.OrderRepository;
import com.app.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderController(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOne(@PathVariable Long id) {
        return orderRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setShippingAddress(request.getShippingAddress());

        double total = 0.0;

        for (CheckoutRequest.CartLine line : request.getItems()) {
            Product product = productRepository.findById(line.productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + line.productId));

            if (product.getStock() < line.quantity) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Not enough stock for: " + product.getName());
            }

            // Deduct stock
            product.setStock(product.getStock() - line.quantity);
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(line.quantity);
            order.getItems().add(item);

            total += product.getPrice() * line.quantity;
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(body.get("status"));
            return ResponseEntity.ok(orderRepository.save(order));
        }).orElse(ResponseEntity.notFound().build());
    }
}
