package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.RestaurantPendingDto;
import com.cemihsankurt.foodAppProject.entity.Restaurant;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAdminController {

    List<RestaurantPendingDto> getRestaurants();

    ResponseEntity<String> approveRestaurant(Long restaurantId);

    ResponseEntity<String> rejectRestaurant(Long restaurantId);
}
