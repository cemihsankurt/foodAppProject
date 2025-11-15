package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.entity.*;
import com.cemihsankurt.foodAppProject.enums.ApprovalStatus;
import com.cemihsankurt.foodAppProject.enums.Role;
import com.cemihsankurt.foodAppProject.repository.ProductRepository;
import com.cemihsankurt.foodAppProject.repository.RestaurantRepository;
import com.cemihsankurt.foodAppProject.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ProductRepository productRepository;

    private Restaurant openRestaurant;

    @BeforeEach
    void setUp(){

        User user1 = new User();
        user1.setEmail("openrestaurant-email@gmail.com");
        user1.setPassword("password1");
        user1.setRole(Role.ROLE_RESTAURANT);
        user1.setVerified(true);
        user1 = userRepository.save(user1);

        openRestaurant = new Restaurant();
        openRestaurant.setName("Open Restaurant");
        openRestaurant.setUser(user1);
        openRestaurant.setApprovalStatus(ApprovalStatus.APPROVED);
        openRestaurant.setAvailable(true);
        openRestaurant = restaurantRepository.save(openRestaurant);

        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal(100.00));
        product1.setRestaurant(openRestaurant);
        productRepository.save(product1);


        User user2 = new User();
        user2.setEmail("pending-restaurant@gmail.com");
        user2.setPassword("password3");
        user2.setRole(Role.ROLE_RESTAURANT);
        user2.setVerified(true);
        user2 = userRepository.save(user2);

        Restaurant pendingRestaurant = new Restaurant();
        pendingRestaurant.setName("Pending Restaurant");
        pendingRestaurant.setUser(user2);
        pendingRestaurant.setApprovalStatus(ApprovalStatus.PENDING);
        pendingRestaurant.setAvailable(false);
        restaurantRepository.save(pendingRestaurant);

    }

    @Test
    void testGetAllRestaurants_ShouldReturn200OkAndOnlyAvailableRestaurants() throws Exception {

        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Open Restaurant"));
    }

    @Test
    void testGetAllRestaurants_ShouldReturnEmptyListWhenNoAvailableRestaurants() throws Exception {

        openRestaurant.setAvailable(false);
        restaurantRepository.save(openRestaurant);

        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetMenuForRestaurant_WhenRestaurantExists_ShouldReturn200OkAndMenu() throws Exception {

        Long restaurantId = openRestaurant.getId();

        mockMvc.perform(get("/api/restaurants/" + restaurantId + "/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"));
    }

    @Test
    void testGetMenuForRestaurant_WhenRestaurantNotFound_ShouldReturn404NotFound() throws Exception {

        Long nonExistentRestaurantId = 999L;

        mockMvc.perform(get("/api/restaurants/" + nonExistentRestaurantId + "/menu"))
                .andExpect(status().isNotFound());
    }



}
