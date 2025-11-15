package com.cemihsankurt.foodAppProject.dto;

import com.cemihsankurt.foodAppProject.entity.ApprovalStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantPendingDto {

    private Long id;

    private String email;

    private String name;

    private String phoneNumber;

    private ApprovalStatus approvalStatus;
}
