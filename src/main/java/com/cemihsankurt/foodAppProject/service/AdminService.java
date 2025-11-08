package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.RestaurantPendingDto;
import com.cemihsankurt.foodAppProject.entity.ApprovalStatus;
import com.cemihsankurt.foodAppProject.entity.Restaurant;
import com.cemihsankurt.foodAppProject.exception.ResourceNotFoundException;
import com.cemihsankurt.foodAppProject.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService{

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public List<RestaurantPendingDto> getPendingRestaurants() {

        List<Restaurant> restaurants = restaurantRepository.findByApprovalStatus(ApprovalStatus.PENDING);

        return restaurants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void approveRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        restaurant.setApprovalStatus(ApprovalStatus.APPROVED);
        restaurantRepository.save(restaurant);
    }

    @Override
    public void rejectRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        restaurant.setApprovalStatus(ApprovalStatus.REJECTED);
        restaurantRepository.save(restaurant);

    }

    private RestaurantPendingDto convertToDto(Restaurant restaurant){

        return RestaurantPendingDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .phoneNumber(restaurant.getPhoneNumber())
                .approvalStatus(restaurant.getApprovalStatus())
                .build();
    }


}
