package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.LoginRequestDto;
import com.cemihsankurt.foodAppProject.dto.RegisterCustomerRequestDto;
import com.cemihsankurt.foodAppProject.dto.RegisterRestaurantRequestDto;
import com.cemihsankurt.foodAppProject.entity.*;
import com.cemihsankurt.foodAppProject.repository.CustomerRepository;
import com.cemihsankurt.foodAppProject.repository.RestaurantRepository;
import com.cemihsankurt.foodAppProject.repository.UserRepository;
import com.cemihsankurt.foodAppProject.repository.VerificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    // Test için kullanacağımız düz metin şifre
    private final String TEST_PASSWORD = "AdminSifresi123";

    @BeforeEach
    void setUp(){

        User adminUser = new User();
        adminUser.setEmail("test-admin@gmail.com");
        adminUser.setPassword(passwordEncoder.encode(TEST_PASSWORD));
        adminUser.setRole(Role.ROLE_ADMIN);
        adminUser.setVerified(true);
        userRepository.save(adminUser);
    }

    @Test
    void testAuthenticateUser_WhenCredentialsAreValid_ShouldReturn200OkAndToken() throws Exception {

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test-admin@gmail.com");
        loginRequestDto.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void testAuthenticateUser_WhenPasswordIsIncorrect_ShouldReturn403Forbidden() throws Exception {

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("test-admin@gmail.com");
        loginRequestDto.setPassword("WrongPassword");

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAuthenticateUser_WhenEmailIsNotVerified_ShouldReturn403Forbidden() throws Exception {

        String unverifiedEmail = "unverifiedEmail@gmail.com";
        String unverifiedPassword = "Unverified123";
        User unverifiedUser = new User();
        unverifiedUser.setEmail(unverifiedEmail);
        unverifiedUser.setPassword(passwordEncoder.encode(unverifiedPassword));
        unverifiedUser.setRole(Role.ROLE_CUSTOMER);
        unverifiedUser.setVerified(false);
        userRepository.save(unverifiedUser);

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail(unverifiedEmail);
        loginRequestDto.setPassword(unverifiedPassword);
        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRegisterCustomer_WhenValidRequest_ShouldReturn201CreatedAndCustomer() throws Exception {

        String newCustomerEmail = "new-customer@gmail.com";
        String newCustomerPassword = "Customer123";

        RegisterCustomerRequestDto registerCustomerRequestDto = new RegisterCustomerRequestDto();
        registerCustomerRequestDto.setEmail(newCustomerEmail);
        registerCustomerRequestDto.setPassword(newCustomerPassword);
        registerCustomerRequestDto.setFirstName("John");
        registerCustomerRequestDto.setLastName("Doe");
        registerCustomerRequestDto.setPhoneNumber("12345678901");

        mockMvc.perform(post("/api/auth/register-customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerCustomerRequestDto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.email").value(newCustomerEmail));

        User createdUser = userRepository.findByEmail(newCustomerEmail).orElse(null);
        assertThat(createdUser.getRole()).isEqualTo(Role.ROLE_CUSTOMER);
        assertThat(createdUser.isVerified()).isFalse();

        Customer createdCustomer = customerRepository.findByUserId(createdUser.getId()).orElse(null);
        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.getFirstName()).isEqualTo("John");
        assertThat(createdCustomer.getLastName()).isEqualTo("Doe");
        assertThat(createdCustomer.getCart()).isNotNull();

        VerificationToken verificationToken = verificationRepository.findByUser(createdUser).orElse(null);
        assertThat(verificationToken.getToken()).isNotBlank();


    }

    @Test
    void testRegisterCustomer_WhenEmailAlreadyExists_ShouldReturn400BadRequest() throws Exception {

        RegisterCustomerRequestDto registerCustomerRequestDto = new RegisterCustomerRequestDto();
        registerCustomerRequestDto.setEmail("test-admin@gmail.com"); // Zaten var olan email
        registerCustomerRequestDto.setPassword("SomePassword123");
        registerCustomerRequestDto.setFirstName("Jane");
        registerCustomerRequestDto.setLastName("Doe");
        registerCustomerRequestDto.setPhoneNumber("09876543210");

        mockMvc.perform(post("/api/auth/register-customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerCustomerRequestDto)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("Email Already Exists"));
    }

    @Test
    void testRegisterRestaurant_WhenValidRequest_ShouldReturn201CreatedAndRestaurant() throws Exception {

        String restaurantEmail = "new-restaurant@gmail.com";
        RegisterRestaurantRequestDto registerRestaurantRequestDto = new RegisterRestaurantRequestDto();
        registerRestaurantRequestDto.setEmail(restaurantEmail);
        registerRestaurantRequestDto.setPassword("Restaurant123");
        registerRestaurantRequestDto.setRestaurantName("New Restaurant");
        registerRestaurantRequestDto.setPhoneNumber("12345678901");

        mockMvc.perform(post("/api/auth/register-restaurant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRestaurantRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(restaurantEmail));

        User createdUser = userRepository.findByEmail(restaurantEmail).orElse(null);
        assertThat(createdUser.getRole()).isEqualTo(Role.ROLE_RESTAURANT);
        assertThat(createdUser.isVerified()).isFalse();

        Restaurant createdRestaurant = restaurantRepository.findByUserId(createdUser.getId()).orElse(null);
        assertThat(createdRestaurant).isNotNull();
        assertThat(createdRestaurant.getName()).isEqualTo("New Restaurant");
        VerificationToken verificationToken = verificationRepository.findByUser(createdUser).orElse(null);
        assertThat(verificationToken.getToken()).isNotBlank();

    }

    @Test
    void testVerifyEmail_WhenTokenIsValid_ShouldReturn200OkAndVerifyUser() throws Exception {

        User unverifiedUser = new User();
        unverifiedUser.setEmail("unverified-user@gmail.com");
        unverifiedUser.setPassword(passwordEncoder.encode("Unverified123"));
        unverifiedUser.setRole(Role.ROLE_CUSTOMER);
        unverifiedUser.setVerified(false);
        unverifiedUser = userRepository.save(unverifiedUser);

        String tokenString = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(tokenString, unverifiedUser);
        verificationRepository.save(verificationToken);

        mockMvc.perform(get("/api/auth/verify")
                        .param("token", tokenString))
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Email verified successfully.")));

        User activatedUser = userRepository.findById(unverifiedUser.getId()).orElse(null);
        assertThat(activatedUser.isVerified()).isTrue();

        Optional<VerificationToken> usedToken = verificationRepository.findByToken(tokenString);
        assertThat(usedToken).isEmpty();
    }

}
