package com.cemihsankurt.foodAppProject.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterRestaurantRequestDto {

    @NotEmpty
    @Email(message = "Geçersiz email formatı")
    private String email;

    @NotEmpty
    @Size(min = 6, message = "Şifre en az 6 karakter uzunluğunda olmalıdır")
    private String password;

    @NotEmpty
    private String restaurantName;

    @NotEmpty(message = "Telefon numarası boş olamaz")
    @Size(min = 11, max = 11, message = "Telefon numarası 11 haneli olmalıdır.")
    private String phoneNumber;

}
