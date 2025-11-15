package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.RestaurantPendingDto;
import com.cemihsankurt.foodAppProject.dto.UserDto;
import com.cemihsankurt.foodAppProject.enums.ApprovalStatus;
import com.cemihsankurt.foodAppProject.entity.Restaurant;
import com.cemihsankurt.foodAppProject.enums.Role;
import com.cemihsankurt.foodAppProject.entity.User;
import com.cemihsankurt.foodAppProject.exception.ResourceNotFoundException;
import com.cemihsankurt.foodAppProject.repository.RestaurantRepository;
import com.cemihsankurt.foodAppProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService{

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<RestaurantPendingDto> getPendingRestaurants() {

        List<Restaurant> restaurants = restaurantRepository.findByApprovalStatus(ApprovalStatus.PENDING);

        return restaurants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public String approveRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        restaurant.setApprovalStatus(ApprovalStatus.APPROVED);
        restaurantRepository.save(restaurant);

        return "Restaurant (ID : " + restaurantId + ") has been approved";
    }

    @Override
    public String rejectRestaurant(Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        restaurant.setApprovalStatus(ApprovalStatus.REJECTED);
        restaurantRepository.save(restaurant);

        return "Restaurant (ID : " + restaurantId + ") has been rejected";

    }

    @Override
    @Transactional
    public String banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() == Role.ROLE_ADMIN) {
            throw new IllegalStateException("Admin kullanıcıları banlanamaz.");
        }

        user.setBanned(!user.isBanned());
        userRepository.save(user);

        return user.isBanned() ?
                "Kullanıcı (ID: " + userId + ") başarıyla banlandı." :
                "Kullanıcı (ID: " + userId + ") banı başarıyla kaldırıldı.";
    }

    @Override
    public List<UserDto> getUsers() {

        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private RestaurantPendingDto convertToDto(Restaurant restaurant){

        return RestaurantPendingDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .email(restaurant.getUser().getEmail())
                .phoneNumber(restaurant.getPhoneNumber())
                .approvalStatus(restaurant.getApprovalStatus())
                .build();
    }

    private UserDto convertToDto(User user){

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isBanned(user.isBanned())
                .isVerified(user.isVerified())
                .build();
    }


}
