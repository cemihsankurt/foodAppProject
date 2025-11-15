package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.RestaurantPendingDto;
import com.cemihsankurt.foodAppProject.dto.UserDto;

import java.util.List;

public interface IAdminService {

    List<RestaurantPendingDto> getPendingRestaurants();

    String approveRestaurant(Long restaurantId);

    String rejectRestaurant(Long restaurantId);

    String banUser(Long userId);

    List<UserDto> getUsers();


}
