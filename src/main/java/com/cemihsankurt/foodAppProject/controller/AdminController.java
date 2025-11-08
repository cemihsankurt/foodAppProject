package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.RestaurantPendingDto;
import com.cemihsankurt.foodAppProject.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController implements IAdminController {

    @Autowired
    private IAdminService adminService;

    @Override
    @GetMapping("/restaurants/pending")
    public List<RestaurantPendingDto> getRestaurants() {

        return adminService.getPendingRestaurants();
    }

    @Override
    @PostMapping("/restaurants/{restaurantId}/approve")
    public ResponseEntity<String> approveRestaurant(@PathVariable  Long restaurantId) {

        adminService.approveRestaurant(restaurantId);
        return ResponseEntity.ok("Restaurant (ID : " + restaurantId + ") has been approved");

    }

    @Override
    @PostMapping("/restaurants/{restaurantId}/reject")
    public ResponseEntity<String> rejectRestaurant(@PathVariable Long restaurantId) {

        adminService.rejectRestaurant(restaurantId);
        return ResponseEntity.ok("Restaurant (ID : " + restaurantId + ") has been rejected");

    }


}
