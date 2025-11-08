package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.OrderDetailsResponseDto;
import com.cemihsankurt.foodAppProject.dto.ProductDto;
import com.cemihsankurt.foodAppProject.entity.OrderStatus;
import com.cemihsankurt.foodAppProject.service.IRestaurantService;
import com.cemihsankurt.foodAppProject.service.OrderService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Data
class StatusUpdateRequest {
    private boolean isAvailable;
}

@Data
class OrderStatusRequest{

    private OrderStatus newStatus;
}

@RestController
@RequestMapping("/api/restaurant-panel")
public class RestaurantPanelController implements IRestaurantPanelController {

    @Autowired
    private IRestaurantService restaurantService;
    @Autowired
    private OrderService orderService;


    @Override
    @PostMapping("/status")
    public void updateAvailability(@RequestBody StatusUpdateRequest request, Authentication authentication) {

        restaurantService.updateAvailability(request.isAvailable(),authentication);
    }

    @Override
    @PostMapping("/menu/products")
    public ResponseEntity<ProductDto> addProductToMenu(@RequestBody ProductDto productDto, Authentication authentication) {

        ProductDto newProduct =  restaurantService.addProductToMenu(productDto,authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @Override
    @DeleteMapping("/menu/products/{productId}")
    public void deleteProductFromMenu(@PathVariable Long productId, Authentication authentication) {

        restaurantService.deleteProductFromMenu(productId,authentication);
    }

    @Override
    @PostMapping("/menu/products/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductDto productDto, Authentication authentication) {

        ProductDto updatedProduct = restaurantService.updateProduct(productId,productDto,authentication);
        return ResponseEntity.ok(updatedProduct);

    }

    @Override
    @PostMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderDetailsResponseDto> updateOrderStatus(@PathVariable Long orderId, @Valid @RequestBody OrderStatusRequest orderStatusRequest, Authentication authentication) {

        OrderDetailsResponseDto response = orderService.updateOrderStatus(orderId,orderStatusRequest.getNewStatus(),authentication);
        return ResponseEntity.ok(response);
    }
}
