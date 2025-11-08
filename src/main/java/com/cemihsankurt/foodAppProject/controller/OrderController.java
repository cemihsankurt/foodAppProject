package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.OrderDetailsResponseDto;
import com.cemihsankurt.foodAppProject.service.CustomerService;
import com.cemihsankurt.foodAppProject.service.IOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController implements IOrderController{

    @Autowired
    private IOrderService orderService;
    @Autowired
    private CustomerService customerService;


    @PostMapping("/create-from-cart")
    @Override
    public ResponseEntity<OrderDetailsResponseDto> createOrder(Authentication authentication) {

        OrderDetailsResponseDto response = orderService.createOrderFromCart(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/my-orders")
    @Override
    public List<OrderDetailsResponseDto> getAllOrders(Authentication authentication) {

        String userEmail = authentication.getName();
        Long customerId = customerService.findCustomerIdByUserEmail(userEmail);
        return orderService.getOrdersByCustomerId(customerId);

    }

    @GetMapping("/{orderId}")
    @Override
    public OrderDetailsResponseDto getOrderById(@PathVariable Long orderId, Authentication authentication) {

        return orderService.getOrderById(orderId,authentication);
    }

    @PutMapping("/{orderId}/cancel")
    @Override
    public OrderDetailsResponseDto deleteOrderById(@PathVariable Long orderId) {

        return orderService.deleteOrderById(orderId);

    }
}
