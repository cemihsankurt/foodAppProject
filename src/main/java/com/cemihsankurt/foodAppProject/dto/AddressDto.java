package com.cemihsankurt.foodAppProject.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDto {

    private String addressTitle;
    private String fullAddress;
}
