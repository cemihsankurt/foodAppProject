package com.cemihsankurt.foodAppProject.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterRestaurantRequestDto {

    @NotEmpty
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotEmpty
    private String restaurantName;

    @NotEmpty
    private String phoneNumber;

}
