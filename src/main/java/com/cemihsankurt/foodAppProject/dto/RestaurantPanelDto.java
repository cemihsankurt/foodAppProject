package com.cemihsankurt.foodAppProject.dto;


import com.cemihsankurt.foodAppProject.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantPanelDto {

    private Long restaurantId;
    private String restaurantName;
    private boolean isAvailable; // Dükkan açık mı?
    private ApprovalStatus approvalStatus; // Onaylandı mı?
    private List<ProductDto> menu; // Restoranın menüsü
}
