package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.ProductDto;
import com.cemihsankurt.foodAppProject.dto.RestaurantDto;
import com.cemihsankurt.foodAppProject.service.IRestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private IRestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants() {

        List<RestaurantDto> restaurants = restaurantService.getAvailableRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<ProductDto>> getMenuForRestaurant(@PathVariable Long restaurantId) {

        List<ProductDto> menu = restaurantService.getMenuForRestaurant(restaurantId);
        return ResponseEntity.ok(menu);
    }


}
