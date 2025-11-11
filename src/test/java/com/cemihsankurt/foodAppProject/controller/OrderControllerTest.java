package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.CreateOrderRequestDto;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;//Sahte HTTP istekleri için

    @Autowired
    private ObjectMapper mapper;//Java nesnelerini JSON a çevirmek için

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
                                                //token işlemleri için
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //Veritabanı

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    private Customer testCustomer;
    private Address testAddress;
    private String customerToken;
    private String restaurantToken;
    private Order testOrder;

    @BeforeEach
    void setUp(){

        // --- Test Verilerimizi Tutacak Değişkenler ---
        User testCustomerUser = new User();
        testCustomerUser.setEmail("test-customer@email.com");
        testCustomerUser.setPassword(passwordEncoder.encode("123456"));
        testCustomerUser.setRole(Role.ROLE_CUSTOMER);
        testCustomerUser.setVerified(true);
        testCustomerUser = userRepository.save(testCustomerUser);

        Cart cart = new Cart();
        testCustomer = new Customer();
        testCustomer.setUser(testCustomerUser);
        testCustomer.setCart(cart);
        cart.setCustomer(testCustomer);
        testCustomer = customerRepository.save(testCustomer);

        testAddress = new Address();
        testAddress.setCustomer(testCustomer);
        testAddress.setAddressTitle("Evim");
        testAddress.setFullAddress("Test Mah. 123 Sk. No:4 Bornova/İzmir");
        testAddress = addressRepository.save(testAddress);

        User testRestaurantUser = new User();
        testRestaurantUser.setEmail("test-restaurant@gmail.com");
        testRestaurantUser.setPassword(passwordEncoder.encode("123456"));
        testRestaurantUser.setRole(Role.ROLE_RESTAURANT);
        testRestaurantUser.setVerified(true);
        testRestaurantUser = userRepository.save(testRestaurantUser);

        Restaurant restaurant = new Restaurant();
        restaurant.setUser(testRestaurantUser);
        restaurant.setName("Test Restaurant");
        restaurant.setApprovalStatus(ApprovalStatus.APPROVED);
        restaurant.setAvailable(true);
        restaurant = restaurantRepository.save(restaurant);

        Product product = new Product();
        product.setRestaurant(restaurant);
        product.setName("Test Product");
        product.setPrice(new BigDecimal(200.00));
        product = productRepository.save(product);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItemRepository.save(cartItem);
        cart.getCartItems().add(cartItem);

        Optional<Restaurant> restaurant1 = restaurantRepository.findByName("Test Restaurant");

        Order order = new Order();
        order.setCustomer(testCustomer);
        order.setRestaurant(restaurant1.get());
        order.setDeliveryAddress(testAddress.getFullAddress());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTotalPrice(new BigDecimal(300.00));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductName(product.getName());
        orderItem.setPrice(new BigDecimal(150.00));
        orderItem.setQuantity(2);

        order.setOrderItems(new ArrayList<>());
        order.getOrderItems().add(orderItem);

        testOrder = orderRepository.save(order);


        customerToken = jwtTokenProvider.generateToken(testCustomerUser);

        System.out.println("### SETUP: customerToken üretildi, email: " + testCustomerUser.getEmail());


        restaurantToken = jwtTokenProvider.generateToken(testRestaurantUser);

        System.out.println("### SETUP: restaurantToken üretildi, email: " + testRestaurantUser.getEmail());

    }


    @Test
    void testCreateOrderFromCart_WhenValid_ShouldReturn201Created() throws Exception {

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setAddressId(testAddress.getId());

        mockMvc.perform(post("/api/orders/create-from-cart")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.restaurantName").value("Test Restaurant"));

    }

    @Test
    void testCreateOrderFromCart_WhenCartIsEmpty_ShouldReturn400BadRequest() throws Exception {

        testCustomer.getCart().getCartItems().clear();
        customerRepository.save(testCustomer);

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setAddressId(testAddress.getId());

        mockMvc.perform(post("/api/orders/create-from-cart")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cart is empty. Cannot create order."));
    }

    @Test
    void testCreateOrderFromCart_WhenAddressDoesNotBelongToCustomer_ShouldReturn403Forbidden() throws Exception {

        User anotherUser = new User();
        anotherUser.setEmail("anotheruser-email@gmail.com");
        anotherUser.setPassword(passwordEncoder.encode("123456"));
        anotherUser.setRole(Role.ROLE_CUSTOMER);
        anotherUser.setVerified(true);
        anotherUser = userRepository.save(anotherUser);

        Customer anotherCustomer = new Customer();
        anotherCustomer.setUser(anotherUser);
        anotherCustomer = customerRepository.save(anotherCustomer);
        Address anotherAddress = new Address();
        anotherAddress.setCustomer(anotherCustomer);
        anotherAddress.setAddressTitle("Başka Ev");
        anotherAddress.setFullAddress("Farklı Mah. 456 Sk. No:7 Kadıköy/İstanbul");
        anotherAddress = addressRepository.save(anotherAddress);

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setAddressId(anotherAddress.getId());

        mockMvc.perform(post("/api/orders/create-from-cart")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateOrderFromCart_WhenRestaurantIsNotAvailable_ShouldReturn400BadRequest() throws Exception {

        Optional<Restaurant> restaurantOpt = restaurantRepository.findByName("Test Restaurant");
        if (restaurantOpt.isPresent()) {
            Restaurant restaurant = restaurantOpt.get();
            restaurant.setAvailable(false);
            restaurantRepository.save(restaurant);
        }

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setAddressId(testAddress.getId());

        mockMvc.perform(post("/api/orders/create-from-cart")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Restaurant" + restaurantOpt.get().getName() + " is not available for orders."));
    }

    @Test
    void testCreateOrderFromCart_WhenNotAuthenticated_ShouldReturn403Forbidden() throws Exception {

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setAddressId(testAddress.getId());

        mockMvc.perform(post("/api/orders/create-from-cart")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateOrderFromCart_WhenRoleIsRestaurant_ShouldReturn403Forbidden() throws Exception {

        CreateOrderRequestDto requestDto = new CreateOrderRequestDto();
        requestDto.setAddressId(testAddress.getId());

        mockMvc.perform(post("/api/orders/create-from-cart")
                .header("Authorization", "Bearer " + restaurantToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetOrderById_WhenCustomerOwnsOrder_ShouldReturn200Ok() throws Exception {

        Long orderId = testOrder.getId();
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.customerName").value(testCustomer.getFirstName() + " " + testCustomer.getLastName()));

    }

    @Test
    void testGetOrderById_WhenOrderDoesNotExist_ShouldReturn404NotFound() throws Exception {

        Long nonExistentOrderId = 9999L;
        mockMvc.perform(get("/api/orders/{orderId}", nonExistentOrderId)
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: " + nonExistentOrderId));

    }


    @Test
    void testGetAllOrders_WhenCustomerHasOrders_ShouldReturn200OkAndOrderList() throws Exception {

        mockMvc.perform(get("/api/orders/my-orders")
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$")).isArray())
                .andExpect(jsonPath("$[0].orderId").value(testOrder.getId()))
                .andExpect(jsonPath("$[0].restaurantName").value("Test Restaurant"));

    }

    @Test
    void testCancelOrder_WhenOrderIsPending_ShouldReturn200Ok() throws Exception {


        Long orderId = testOrder.getId();

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId)
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.orderStatus").value("CANCELLED"));
    }

    @Test
    void testCancelOrder_WhenOrderIsNotPending_ShouldReturn400BadRequest() throws Exception {

        testOrder.setOrderStatus(OrderStatus.COMPLETED);
        testOrder = orderRepository.save(testOrder);
        Long orderId = testOrder.getId();

        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId)
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Only pending orders can be cancelled."));
    }

    @Test
    void testCancelOrder_WhenCustomerDoesNotOwnOrder_ShouldReturn403Forbidden() throws Exception {

        User anotherUser = new User();
        anotherUser.setEmail("hacker-email@gmail.com");
        anotherUser.setPassword(passwordEncoder.encode("123456"));
        anotherUser.setRole(Role.ROLE_CUSTOMER);
        anotherUser.setVerified(true);
        anotherUser = userRepository.save(anotherUser);

        Customer anotherCustomer = new Customer();
        anotherCustomer.setUser(anotherUser);
        anotherCustomer = customerRepository.save(anotherCustomer);

        String hackerToken = jwtTokenProvider.generateToken(anotherUser);

        mockMvc.perform(post("/api/orders/{orderId}/cancel", testOrder.getId())
                        .header("Authorization", "Bearer " + hackerToken))
                .andExpect(status().isForbidden());
    }





}
