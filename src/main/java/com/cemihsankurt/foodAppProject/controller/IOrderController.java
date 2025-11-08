package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.CreateOrderRequestDto;
import com.cemihsankurt.foodAppProject.dto.OrderDetailsResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.List;

public interface IOrderController {

    ResponseEntity<OrderDetailsResponseDto> createOrder(CreateOrderRequestDto createOrderRequestDto,Authentication authentication);

    List<OrderDetailsResponseDto> getAllOrders(Authentication authentication);

    OrderDetailsResponseDto getOrderById(Long orderId, Authentication authentication);

    OrderDetailsResponseDto deleteOrderById(Long orderId);


}
