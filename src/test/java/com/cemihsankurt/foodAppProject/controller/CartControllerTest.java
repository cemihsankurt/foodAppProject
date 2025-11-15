package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.entity.*;
import com.cemihsankurt.foodAppProject.enums.ApprovalStatus;
import com.cemihsankurt.foodAppProject.enums.Role;
import com.cemihsankurt.foodAppProject.repository.*;
import com.cemihsankurt.foodAppProject.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
class CartControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    // --- Gerekli Tüm Repository'ler ---
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // --- Test Verilerimiz ---
    private String customerToken;
    private Customer testCustomer;
    private Product testProduct;
    private String restaurantToken;

    @BeforeEach
    void setUp() {

        User customerUser = new User();
        customerUser.setEmail("customer1@gmail.com");
        customerUser.setPassword(passwordEncoder.encode("customer123"));
        customerUser.setRole(Role.ROLE_CUSTOMER);
        userRepository.save(customerUser);

        Cart cart = new Cart();
        testCustomer = new Customer();
        testCustomer.setUser(customerUser);
        testCustomer.setCart(cart);
        cart.setCustomer(testCustomer);
        customerRepository.save(testCustomer);

        customerToken = jwtTokenProvider.generateToken(customerUser);

        User restaurantUser = new User();
        restaurantUser.setEmail("restaurant1@gmail.com");
        restaurantUser.setPassword(passwordEncoder.encode("restaurant123"));
        restaurantUser.setRole(Role.ROLE_RESTAURANT);
        userRepository.save(restaurantUser);

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setUser(restaurantUser);
        restaurant.setApprovalStatus(ApprovalStatus.APPROVED);
        restaurant.setAvailable(true);
        restaurantRepository.save(restaurant);

        restaurantToken = jwtTokenProvider.generateToken(restaurantUser);

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setRestaurant(restaurant);
        testProduct.setPrice(new BigDecimal(100.00));
        productRepository.save(testProduct);

    }

    @Test
    void testAddProductTocCart_WhenCartIsEmpty_ShouldReturn200Ok() throws Exception {

        CartController.AddToCartRequest requestDto = new CartController.AddToCartRequest();
        requestDto.setProductId(testProduct.getId());
        requestDto.setQuantity(2);

        mockMvc.perform(post("/api/cart/add")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(testProduct.getId()))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.cartTotal").value(200.00));

    }

    @Test
    void testAddProductToCart_WhenProductAlreadyInCart_ShouldUpdateQuantity() throws Exception {

        CartController.AddToCartRequest initialRequest = new CartController.AddToCartRequest();
        initialRequest.setProductId(testProduct.getId());
        initialRequest.setQuantity(2);

        mockMvc.perform(post("/api/cart/add")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initialRequest)))
                .andExpect(status().isOk());

        CartController.AddToCartRequest updateRequest = new CartController.AddToCartRequest();
        updateRequest.setProductId(testProduct.getId());
        updateRequest.setQuantity(3);

        mockMvc.perform(post("/api/cart/add")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(testProduct.getId()))
                .andExpect(jsonPath("$.items[0].quantity").value(5)) // 2 + 3 = 5
                .andExpect(jsonPath("$.cartTotal").value(500.00)); // 5 * 100.00

    }

    @Test
    void testAddProductToCart_WhenRoleIsRestaurant_ShouldReturn403Forbidden() throws Exception {

        CartController.AddToCartRequest requestDto = new CartController.AddToCartRequest();
        requestDto.setProductId(testProduct.getId());
        requestDto.setQuantity(2);

        mockMvc.perform(post("/api/cart/add")
                        .header("Authorization", "Bearer " + restaurantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());

    }

    @Test
    void testAddProductToCart_WhenProductDoesNotExist_ShouldReturn404NotFound() throws Exception {

        CartController.AddToCartRequest requestDto = new CartController.AddToCartRequest();
        requestDto.setProductId(9999L); // Geçersiz ürün ID'si
        requestDto.setQuantity(2);

        mockMvc.perform(post("/api/cart/add")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));


    }

    @Test
    void testRemoveProductFromCart_WhenProductInCart_ShouldReturn200OkAndUpdatedCart() throws Exception {

        CartController.AddToCartRequest addRequest = new CartController.AddToCartRequest();
        addRequest.setProductId(testProduct.getId());
        addRequest.setQuantity(2);

        mockMvc.perform(post("/api/cart/add")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/cart/remove/{productId}", testProduct.getId())
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.cartTotal").value(0.00));

    }

    @Test
    void testRemoveFromCart_WhenItemNotFound_ShouldReturn404NotFound() throws Exception {

        Long nonExistentProductIdInCart = testProduct.getId();

        mockMvc.perform(delete("/api/cart/remove/" + nonExistentProductIdInCart)
                        .header("Authorization", "Bearer " + customerToken))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message").value("Item not found in the cart"));
    }

    @Test
    void testGetMyCard_WhenCartIsEmpty_ShouldReturn200OkAndEmptyCart() throws Exception {

        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.cartTotal").value(0.00));
    }

    @Test
    void testGetMyCard_WhenCartHasItems_ShouldReturn200OkAndCartContents() throws Exception {

        CartController.AddToCartRequest addRequest = new CartController.AddToCartRequest();
        addRequest.setProductId(testProduct.getId());
        addRequest.setQuantity(3);

        mockMvc.perform(post("/api/cart/add")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + customerToken))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.items.length()").value(1))
                        .andExpect(jsonPath("$.items[0].productId").value(testProduct.getId()))
                        .andExpect(jsonPath("$.items[0].quantity").value(3))
                        .andExpect(jsonPath("$.cartTotal").value(300.00));
    }
}
