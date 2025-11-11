package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.CreateOrderRequestDto;
import com.cemihsankurt.foodAppProject.dto.OrderDetailsResponseDto;
import com.cemihsankurt.foodAppProject.entity.Order;
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
public class OrderController implements IOrderController {

    @Autowired
    private IOrderService orderService;
    @Autowired
    private CustomerService customerService;


    @PostMapping("/create-from-cart")
    public ResponseEntity<OrderDetailsResponseDto> createOrder(
            @Valid @RequestBody CreateOrderRequestDto createOrderRequestDto,
            Authentication authentication) {

        OrderDetailsResponseDto response = orderService.createOrderFromCart(authentication, createOrderRequestDto.getAddressId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDetailsResponseDto>> getAllOrders(Authentication authentication) {

        List<OrderDetailsResponseDto> response = orderService.getOrdersByCustomerId(authentication);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsResponseDto> getOrderById(@PathVariable Long orderId, Authentication authentication) {

        OrderDetailsResponseDto response = orderService.getOrderById(orderId, authentication);
        return ResponseEntity.ok(response);


    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDetailsResponseDto> cancelOrder(@PathVariable Long orderId, Authentication authentication) {


        OrderDetailsResponseDto response = orderService.cancelOrder(orderId, authentication);
        return ResponseEntity.ok(response);
    }
}
