package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.RestaurantPendingDto;

import java.util.List;

public interface IAdminService {

    List<RestaurantPendingDto> getPendingRestaurants();

    void approveRestaurant(Long restaurantId);

    void rejectRestaurant(Long restaurantId);


}
