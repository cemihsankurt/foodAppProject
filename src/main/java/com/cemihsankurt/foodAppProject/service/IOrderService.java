package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.CreateOrderRequestDto;
import com.cemihsankurt.foodAppProject.dto.OrderDetailsResponseDto;
import com.cemihsankurt.foodAppProject.entity.OrderStatus;
import com.cemihsankurt.foodAppProject.entity.Restaurant;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IOrderService {

    OrderDetailsResponseDto createOrderFromCart(Authentication authentication, Long addressId);

    List<OrderDetailsResponseDto> getOrdersByCustomerId(Long customerId);

    OrderDetailsResponseDto getOrderById(Long orderId, Authentication authentication);

    OrderDetailsResponseDto deleteOrderById(Long orderId);

    List<OrderDetailsResponseDto> getOrdersByRestaurant(Restaurant restaurant);

    OrderDetailsResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus, Authentication authentication);
}
