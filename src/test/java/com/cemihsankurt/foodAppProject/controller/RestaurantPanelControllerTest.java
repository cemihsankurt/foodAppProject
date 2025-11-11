package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.ProductDto;
import com.cemihsankurt.foodAppProject.entity.*;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RestaurantPanelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AddressRepository addressRepository;

    private String myRestaurantToken; // Kendi restoranımızın token'ı
    private Restaurant myRestaurant;

    private String otherRestaurantToken; // Başka bir restoranın token'ı
    private Product otherRestaurantProduct;// Başka bir restoranın ürünü
    private Product myRestaurantProduct;// Kendi restoranımızın ürünü
    private Order testOrder;

    @BeforeEach
    void setUp(){

        User myUser = new User();
        myUser.setEmail("my-restaurant@gmail.com");
        myUser.setPassword(passwordEncoder.encode("my-restaurant-password"));
        myUser.setVerified(true);
        myUser.setRole(Role.ROLE_RESTAURANT);
        userRepository.save(myUser);

        myRestaurant = new Restaurant();
        myRestaurant.setName("My Restaurant");
        myRestaurant.setUser(myUser);
        myRestaurant.setApprovalStatus(ApprovalStatus.APPROVED);
        myRestaurant.setAvailable(false);
        restaurantRepository.save(myRestaurant);

        myRestaurantToken = jwtTokenProvider.generateToken(myUser);

        myRestaurantProduct = new Product();
        myRestaurantProduct.setRestaurant(myRestaurant);
        myRestaurantProduct.setName("My Restaurant Product");
        myRestaurantProduct.setDescription("Delicious product from my restaurant");
        myRestaurantProduct.setPrice(new BigDecimal(75));
        myRestaurantProduct = productRepository.save(myRestaurantProduct);

        User otherUser = new User();
        otherUser.setEmail("other-restaurant@gmail.com");
        otherUser.setPassword(passwordEncoder.encode("other-restaurant-password"));
        otherUser.setVerified(true);
        otherUser.setRole(Role.ROLE_RESTAURANT);
        userRepository.save(otherUser);

        Restaurant otherRestaurant = new Restaurant();
        otherRestaurant.setName("Other Restaurant");
        otherRestaurant.setUser(otherUser);
        otherRestaurant.setApprovalStatus(ApprovalStatus.APPROVED);
        otherRestaurant.setAvailable(true);
        restaurantRepository.save(otherRestaurant);

        otherRestaurantProduct = new Product();
        otherRestaurantProduct.setName("Other Restaurant Product");
        otherRestaurantProduct.setDescription("Delicious product from other restaurant");
        otherRestaurantProduct.setPrice(new BigDecimal(100));
        otherRestaurantProduct.setRestaurant(otherRestaurant);
        productRepository.save(otherRestaurantProduct);

        otherRestaurantToken = jwtTokenProvider.generateToken(otherUser);

        User customerUser = new User();
        customerUser.setEmail("customer-user@gmail.com");
        customerUser.setPassword(passwordEncoder.encode("customer-user-password"));
        customerUser.setVerified(true);
        customerUser.setRole(Role.ROLE_CUSTOMER);
        userRepository.save(customerUser);

        Customer customer = new Customer();
        customer.setUser(customerUser);
        customer = customerRepository.save(customer);

        Address address = new Address();
        address.setCustomer(customer);
        address.setFullAddress("Test Address 123");
        address.setAddressTitle("Test Address Title");
        addressRepository.save(address);

        testOrder = new Order();
        testOrder.setCustomer(customer);
        testOrder.setRestaurant(myRestaurant);
        testOrder.setDeliveryAddress("Test Address 123");
        testOrder.setOrderStatus(OrderStatus.PENDING);
        testOrder.setTotalPrice(myRestaurantProduct.getPrice().multiply(BigDecimal.valueOf(1)));
        testOrder.setOrderItems(new ArrayList<>());

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(testOrder);
        orderItem.setProductName(myRestaurantProduct.getName());
        orderItem.setQuantity(1);
        orderItem.setPrice(myRestaurantProduct.getPrice());

        testOrder.getOrderItems().add(orderItem);
        testOrder = orderRepository.save(testOrder);


    }

    @Test
    void testAddProductToMenu_WhenOwner_ShouldReturn201Created() throws Exception{

        ProductDto newProductDto = new ProductDto();
        newProductDto.setName("New Product");
        newProductDto.setDescription("Delicious new product");
        newProductDto.setPrice(new BigDecimal(50));

        mockMvc.perform(post("/api/restaurant-panel/menu/products")
                .header("Authorization","Bearer " + myRestaurantToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProductDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.description").value("Delicious new product"))
                .andExpect(jsonPath("$.price").value(50));


    }

    @Test
    void testDeleteProductFromMenu_WhenNotOwner_ShouldReturn403Forbidden() throws Exception {

        Long victimProductId = otherRestaurantProduct.getId();

        mockMvc.perform(delete("/api/restaurant-panel/menu/products/" + victimProductId)
                        .header("Authorization", "Bearer " + myRestaurantToken))
                        .andExpect(status().isForbidden());
    }

    @Test
    void testAddProductToMenu_WhenCustomer_ShouldReturn403Forbidden() throws Exception {

        User customerUser = new User();
        customerUser.setEmail("customer-usertest@gmail.com");
        customerUser.setPassword(passwordEncoder.encode("customer-user-password"));
        customerUser.setVerified(true);
        customerUser.setRole(Role.ROLE_CUSTOMER);
        userRepository.save(customerUser);
        String customerToken = jwtTokenProvider.generateToken(customerUser);
        ProductDto newProductDto = new ProductDto();

        mockMvc.perform(post("/api/restaurant-panel/menu/products")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProductDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateProduct_WhenOwner_ShouldReturn200Ok() throws Exception {

        Long productIdToUpdate = myRestaurantProduct.getId();

        ProductDto updatedProductDto = new ProductDto();
        updatedProductDto.setName("Updated Product Name");
        updatedProductDto.setDescription("Updated Description");
        updatedProductDto.setPrice(new BigDecimal(150));

        mockMvc.perform(post("/api/restaurant-panel/menu/products/" + productIdToUpdate)
                        .header("Authorization", "Bearer " + myRestaurantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product Name"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.price").value(150));

        Product updatedProduct = productRepository.findById(productIdToUpdate)
                .orElseThrow(() -> new RuntimeException("Product not found in the test db"));

        assertThat(updatedProduct.getName().equals("Updated Product Name"));
        assertThat(updatedProduct.getPrice()).isEqualByComparingTo(new BigDecimal(150));

    }

    @Test
    void testUpdateAvailability_WhenApproved_ShouldReturn200Ok() throws Exception {

        Long restaurantId = myRestaurant.getId();
        StatusUpdateRequest statusUpdateRequest = new StatusUpdateRequest();
        statusUpdateRequest.setAvailable(true);

        mockMvc.perform(post("/api/restaurant-panel/status")
                        .header("Authorization", "Bearer " + myRestaurantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateRequest)))
                        .andExpect(status().isOk());

        Restaurant updatedRestaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found in the test db"));

        assertThat(updatedRestaurant.isAvailable()).isTrue();

    }

    @Test
    void testUpdateAvailability_WhenPending_ShouldReturn400BadRequest() throws Exception {

        myRestaurant.setApprovalStatus(ApprovalStatus.PENDING);
        restaurantRepository.save(myRestaurant);

        StatusUpdateRequest statusUpdateRequest = new StatusUpdateRequest();
        statusUpdateRequest.setAvailable(true);

        mockMvc.perform(post("/api/restaurant-panel/status")
                        .header("Authorization", "Bearer " + myRestaurantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateRequest)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("Only approved restaurants can update availability status."));

    }

    @Test
    void testUpdateOrderStatus_WhenOwner_ShouldReturn200OkAndChangeStatus() throws Exception {

        OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
        orderStatusRequest.setNewStatus(OrderStatus.PREPARING);

        mockMvc.perform(post("/api/restaurant-panel/orders/{orderId}/status", testOrder.getId())
                        .header("Authorization", "Bearer " + myRestaurantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderStatusRequest)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.orderStatus").value("PREPARING"));

        Order updatedOrder = orderRepository.findById(testOrder.getId()).get();
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.PREPARING);

    }

    @Test
    void testUpdateOrderStatus_WhenNotOwner_ShouldReturn403Forbidden() throws Exception {

        OrderStatusRequest orderStatusRequest = new OrderStatusRequest();
        orderStatusRequest.setNewStatus(OrderStatus.PREPARING);

        mockMvc.perform(post("/api/restaurant-panel/orders/{orderId}/status", testOrder.getId())
                        .header("Authorization", "Bearer " + otherRestaurantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderStatusRequest)))
                        .andExpect(status().isForbidden());

    }

}
