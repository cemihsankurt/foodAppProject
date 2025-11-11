package com.cemihsankurt.foodAppProject.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterCustomerRequestDto {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

    @NotEmpty
    @Pattern(regexp = "\\d{11}", message = "Phone number must be 11 digits")
    private String phoneNumber;


}
