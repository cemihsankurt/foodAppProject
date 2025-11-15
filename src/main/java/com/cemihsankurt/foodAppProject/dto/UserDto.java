package com.cemihsankurt.foodAppProject.dto;

import com.cemihsankurt.foodAppProject.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private Long id;
    private String email;
    private Role role;
    private boolean isBanned;
    private boolean isVerified;

}
