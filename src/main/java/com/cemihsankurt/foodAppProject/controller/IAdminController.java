package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.RestaurantPendingDto;
import com.cemihsankurt.foodAppProject.dto.UserDto;
import com.cemihsankurt.foodAppProject.entity.Restaurant;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAdminController {

    ResponseEntity<List<RestaurantPendingDto>> getPendingRestaurants();

    ResponseEntity<String> approveRestaurant(Long restaurantId);

    ResponseEntity<String> rejectRestaurant(Long restaurantId);

    ResponseEntity<String> banUser(Long userId);

    ResponseEntity<List<UserDto>> getUsers();
}
