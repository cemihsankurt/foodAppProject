package com.cemihsankurt.foodAppProject.service;


import com.cemihsankurt.foodAppProject.dto.OrderDetailsResponseDto;
import com.cemihsankurt.foodAppProject.entity.OrderStatus;
import com.cemihsankurt.foodAppProject.entity.Restaurant;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IOrderService {

    OrderDetailsResponseDto createOrderFromCart(Authentication authentication, Long addressId);

    List<OrderDetailsResponseDto> getOrdersByCustomerId(Authentication authentication);

    OrderDetailsResponseDto getOrderById(Long orderId, Authentication authentication);

    OrderDetailsResponseDto cancelOrder(Long orderId,Authentication authentication);

    List<OrderDetailsResponseDto> getOrdersByRestaurant(Authentication authentication);

    OrderDetailsResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus, Authentication authentication);

}
