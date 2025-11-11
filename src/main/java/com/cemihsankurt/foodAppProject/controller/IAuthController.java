package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.*;
import org.springframework.http.ResponseEntity;

public interface IAuthController {

    ResponseEntity<UserResponseDto> registerRestaurant(RegisterRestaurantRequestDto registerRestaurantRequestDto);

    AuthResponseDto authenticateUser(LoginRequestDto loginRequestDto);

    ResponseEntity<UserResponseDto> registerCustomer(RegisterCustomerRequestDto registerCustomerRequestDto);

    ResponseEntity<String> verifyEmail(String token);
}
