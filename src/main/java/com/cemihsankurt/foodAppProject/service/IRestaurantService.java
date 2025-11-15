package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.ProductDto;
import com.cemihsankurt.foodAppProject.dto.RestaurantDto;
import com.cemihsankurt.foodAppProject.dto.RestaurantPanelDto;
import com.cemihsankurt.foodAppProject.entity.Restaurant;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IRestaurantService {

    void updateAvailability(boolean isAvailable, Authentication authentication);

    ProductDto addProductToMenu(ProductDto productDto, Authentication authentication);

    void deleteProductFromMenu(Long productId, Authentication authentication);

    ProductDto updateProduct(Long productId,ProductDto productDto, Authentication authentication);

    List<ProductDto> getMenuForRestaurant(Long restaurantId);

    List<RestaurantDto> getAvailableRestaurants();

    RestaurantPanelDto getMyPanelDetails(Authentication authentication);



}
