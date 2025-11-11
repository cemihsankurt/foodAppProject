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
    public ResponseEntity<List<RestaurantPendingDto>> getPendingRestaurants() {

        List<RestaurantPendingDto> response = adminService.getPendingRestaurants();
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/restaurants/{restaurantId}/approve")
    public ResponseEntity<String> approveRestaurant(@PathVariable  Long restaurantId) {

        String response = adminService.approveRestaurant(restaurantId);
        return ResponseEntity.ok(response);

    }

    @Override
    @PostMapping("/restaurants/{restaurantId}/reject")
    public ResponseEntity<String> rejectRestaurant(@PathVariable Long restaurantId) {

        String response = adminService.rejectRestaurant(restaurantId);
        return ResponseEntity.ok(response);

    }


}
