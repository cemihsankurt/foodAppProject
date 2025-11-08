package com.cemihsankurt.foodAppProject.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDto {

    private String name;
    private String description;
    private BigDecimal price;
}
