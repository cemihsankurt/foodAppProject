package com.cemihsankurt.foodAppProject.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderRequestDto {

    @NotNull
    private Long addressId;
}
