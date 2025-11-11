package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.CreateOrderRequestDto;
import com.cemihsankurt.foodAppProject.dto.OrderDetailsResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.List;

public interface IOrderController {

    ResponseEntity<OrderDetailsResponseDto> createOrder(CreateOrderRequestDto createOrderRequestDto,Authentication authentication);

    ResponseEntity<List<OrderDetailsResponseDto>> getAllOrders(Authentication authentication);

    ResponseEntity<OrderDetailsResponseDto> getOrderById(Long orderId, Authentication authentication);

    ResponseEntity<OrderDetailsResponseDto> cancelOrder(Long orderId,Authentication authentication);


}
