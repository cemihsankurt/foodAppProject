package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.OrderDetailsResponseDto;
import com.cemihsankurt.foodAppProject.dto.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface IRestaurantPanelController {

    void updateAvailability(StatusUpdateRequest isAvailable, Authentication authentication);

    ResponseEntity<ProductDto> addProductToMenu(ProductDto productDto, Authentication authentication);

    void deleteProductFromMenu(Long productId, Authentication authentication);

    ResponseEntity<ProductDto> updateProduct(Long productId, ProductDto productDto, Authentication authentication);

    ResponseEntity<OrderDetailsResponseDto> updateOrderStatus(Long orderId, OrderStatusRequest orderStatusRequest, Authentication authentication);
}
