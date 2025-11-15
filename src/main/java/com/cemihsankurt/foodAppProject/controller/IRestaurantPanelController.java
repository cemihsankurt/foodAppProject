package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.OrderDetailsResponseDto;
import com.cemihsankurt.foodAppProject.dto.ProductDto;
import com.cemihsankurt.foodAppProject.dto.RestaurantPanelDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IRestaurantPanelController {

    ResponseEntity<Void> updateAvailability(StatusUpdateRequest isAvailable, Authentication authentication);

    ResponseEntity<ProductDto> addProductToMenu(ProductDto productDto, Authentication authentication);

    ResponseEntity<Void> deleteProductFromMenu(Long productId, Authentication authentication);

    ResponseEntity<ProductDto> updateProduct(Long productId, ProductDto productDto, Authentication authentication);

    ResponseEntity<OrderDetailsResponseDto> updateOrderStatus(Long orderId, OrderStatusRequest orderStatusRequest, Authentication authentication);

    ResponseEntity<List<OrderDetailsResponseDto>> getOrders(Authentication authentication);

    ResponseEntity<RestaurantPanelDto> getMyPanelDetails(Authentication authentication);
}
