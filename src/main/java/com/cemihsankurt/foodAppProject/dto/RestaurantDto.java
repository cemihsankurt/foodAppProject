package com.cemihsankurt.foodAppProject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantDto {

    private Long id;
    private String name;
}
