package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.*;
import com.cemihsankurt.foodAppProject.service.IAuthService;
import com.cemihsankurt.foodAppProject.service.IEmailService;
import com.cemihsankurt.foodAppProject.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<UserResponseDto> registerRestaurant(@Valid  @RequestBody RegisterRestaurantRequestDto registerRestaurantRequestDto) {

        UserResponseDto response = authService.registerRestaurant(registerRestaurantRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }



    @PostMapping("/register-customer")
    @Override
    public ResponseEntity<UserResponseDto> registerCustomer(@Valid @RequestBody RegisterCustomerRequestDto registerCustomerRequestDto) {

        UserResponseDto response = authService.registerCustomer(registerCustomerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/authenticate")
    @Override
    public AuthResponseDto authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        return authService.authenticate(loginRequestDto);
    }

    @GetMapping("/verify")
    @Override
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {

        String response = authService.verifyToken(token);
        return ResponseEntity.ok(response);

    }


}
