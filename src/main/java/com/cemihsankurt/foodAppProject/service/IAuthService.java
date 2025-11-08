package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.*;

public interface IAuthService {

    UserResponseDto registerRestaurant(RegisterRestaurantRequestDto registerRestaurantRequestDto);

    AuthResponseDto authenticate(LoginRequestDto loginRequestDto);

    UserResponseDto registerCustomer(RegisterCustomerRequestDto registerCustomerRequestDto);

    String verifyToken(String token);






}
