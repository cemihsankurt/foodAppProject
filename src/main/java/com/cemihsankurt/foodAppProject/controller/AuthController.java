package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.*;
import com.cemihsankurt.foodAppProject.service.IAuthService;
import com.cemihsankurt.foodAppProject.service.IEmailService;
import com.cemihsankurt.foodAppProject.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController implements IAuthController{

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuthService authService;


    @PostMapping("/register-restaurant")
    @Override
    public UserResponseDto registerRestaurant(@Valid  @RequestBody RegisterRestaurantRequestDto registerRestaurantRequestDto) {

        return authService.registerRestaurant(registerRestaurantRequestDto);

    }



    @PostMapping("/register-customer")
    @Override
    public UserResponseDto registerCustomer(@Valid @RequestBody RegisterCustomerRequestDto registerCustomerRequestDto) {

        return authService.registerCustomer(registerCustomerRequestDto);
    }


    @PostMapping("/authenticate")
    @Override
    public AuthResponseDto authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        return authService.authenticate(loginRequestDto);
    }

    @GetMapping("/verify")
    @Override
    public String verifyEmail(@RequestParam("token") String token) {

        return  authService.verifyToken(token);

    }


}
