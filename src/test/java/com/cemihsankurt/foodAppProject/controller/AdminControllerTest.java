package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.entity.ApprovalStatus;
import com.cemihsankurt.foodAppProject.entity.Restaurant;
import com.cemihsankurt.foodAppProject.entity.Role;
import com.cemihsankurt.foodAppProject.entity.User;
import com.cemihsankurt.foodAppProject.repository.RestaurantRepository;
import com.cemihsankurt.foodAppProject.repository.UserRepository;
import com.cemihsankurt.foodAppProject.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String adminToken;
    private Restaurant pendingRestaurant;

    @BeforeEach
    void setUp(){

        User adminUser = new User();
        adminUser.setEmail("admin-email@gmail.com");
        adminUser.setPassword(passwordEncoder.encode("admin-password"));
        adminUser.setRole(Role.ROLE_ADMIN);
        adminUser.setVerified(true);
        adminUser = userRepository.save(adminUser);

        adminToken = jwtTokenProvider.generateToken(adminUser);

        User restaurantOwner = new User();
        restaurantOwner.setEmail("restaurant-email@gmail.com");
        restaurantOwner.setPassword(passwordEncoder.encode("restaurant-password"));
        restaurantOwner.setRole(Role.ROLE_RESTAURANT);
        restaurantOwner.setVerified(true);
        restaurantOwner = userRepository.save(restaurantOwner);

        pendingRestaurant = new Restaurant();
        pendingRestaurant.setName("Pending Restaurant");
        pendingRestaurant.setUser(restaurantOwner);
        pendingRestaurant.setApprovalStatus(ApprovalStatus.PENDING);
        pendingRestaurant.setAvailable(false);
        pendingRestaurant = restaurantRepository.save(pendingRestaurant);

        User approvedUser = new User();
        approvedUser.setEmail("approved-restaurant@gmail.com");
        approvedUser.setPassword(passwordEncoder.encode("approved-password"));
        approvedUser.setRole(Role.ROLE_RESTAURANT);
        approvedUser.setVerified(true);
        approvedUser = userRepository.save(approvedUser);

        Restaurant approvedRestaurant = new Restaurant();
        approvedRestaurant.setName("Approved Restaurant");
        approvedRestaurant.setUser(approvedUser);
        approvedRestaurant.setApprovalStatus(ApprovalStatus.APPROVED);
        approvedRestaurant.setAvailable(true);
        restaurantRepository.save(approvedRestaurant);

    }

    @Test
    void testGetPendingRestaurants_WhenAdmin_ShouldReturn200OkAndPendingRestaurants() throws Exception {

        mockMvc.perform(get("/api/admin/restaurants/pending")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Pending Restaurant"));
    }

    @Test
    void testGetPendingRestaurants_WhenCustomer_ShouldReturn403Forbidden() throws Exception {

        User normalUser = new User();
        normalUser.setEmail("normal-user@gmail.com");
        normalUser.setPassword(passwordEncoder.encode("normal-password"));
        normalUser.setRole(Role.ROLE_CUSTOMER);
        normalUser.setVerified(true);
        normalUser = userRepository.save(normalUser);

        String customerToken = jwtTokenProvider.generateToken(normalUser);

        mockMvc.perform(get("/api/admin/restaurants/pending")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testApproveRestaurant_WhenAdmin_ShouldReturn200OkAndChangeStatus() throws Exception {

        Long restaurantIdToApprove = pendingRestaurant.getId();

        mockMvc.perform(post("/api/admin/restaurants/{restaurantId}/approve", pendingRestaurant.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("has been approved")));

        Restaurant approvedRestaurant = restaurantRepository.findById(restaurantIdToApprove)
                .orElseThrow(() -> new Exception("Restoran testte bulunamadı!"));

        assertThat(approvedRestaurant.getApprovalStatus()).isEqualTo(ApprovalStatus.APPROVED);
    }

    @Test
    void testRejectRestaurant_WhenAdmin_ShouldReturn200OkAndChangeStatus() throws Exception{

        Long restaurantIdToReject = pendingRestaurant.getId();

        mockMvc.perform(post("/api/admin/restaurants/{restaurantId}/reject",pendingRestaurant.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("has been rejected")));

        Restaurant rejectedRestaurant = restaurantRepository.findById(restaurantIdToReject)
                .orElseThrow(() -> new Exception("Restoran testte bulunamadı!"));

        assertThat(rejectedRestaurant.getApprovalStatus()).isEqualTo(ApprovalStatus.REJECTED);
    }


}
