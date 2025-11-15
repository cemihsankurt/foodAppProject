package com.cemihsankurt.foodAppProject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterCustomerRequestDto {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotEmpty
    @Email(message = "Geçersiz email formatı")
    private String email;

    @NotEmpty
    @Size(min = 6,message = "Şifre en az 6 karakter uzunluğunda olmalıdır")
    private String password;

    @NotEmpty(message = "Telefon numarası boş olamaz")
    @Size(min = 11, max = 11, message = "Telefon numarası 11 haneli olmalıdır.")
    private String phoneNumber;


}
