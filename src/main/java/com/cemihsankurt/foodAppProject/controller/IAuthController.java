package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.*;

public interface IAuthController {

    UserResponseDto registerRestaurant(RegisterRestaurantRequestDto registerRestaurantRequestDto);

    AuthResponseDto authenticateUser(LoginRequestDto loginRequestDto);

    UserResponseDto registerCustomer(RegisterCustomerRequestDto registerCustomerRequestDto);

    String verifyEmail(String token);
}
