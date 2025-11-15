package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.AddressDto;
import com.cemihsankurt.foodAppProject.entity.Address;
import com.cemihsankurt.foodAppProject.entity.Customer;
import com.cemihsankurt.foodAppProject.enums.Role;
import com.cemihsankurt.foodAppProject.entity.User;
import com.cemihsankurt.foodAppProject.repository.AddressRepository;
import com.cemihsankurt.foodAppProject.repository.CustomerRepository;
import com.cemihsankurt.foodAppProject.repository.UserRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String customerToken;
    private Customer testCustomer;

    @BeforeEach
    void setUp(){

        User customerUser = new User();
        customerUser.setEmail("customer-user@gmail.com");
        customerUser.setPassword(passwordEncoder.encode("123456"));
        customerUser.setRole(Role.ROLE_CUSTOMER);
        customerUser.setVerified(true);
        userRepository.save(customerUser);

        testCustomer = new Customer();
        testCustomer.setUser(customerUser);
        testCustomer.setFirstName("Customer");
        testCustomer.setLastName("User");
        customerRepository.save(testCustomer);

        customerToken = jwtTokenProvider.generateToken(customerUser);
    }

    @Test
    void testAddAddress_WhenValid_ShouldReturn201Created() throws  Exception {

        AddressDto addressDto = new AddressDto();
        addressDto.setAddressTitle("Home");
        addressDto.setFullAddress("123 Main St, City, Country");

        mockMvc.perform(post("/api/customer/addresses")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressDto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.addressTitle").value("Home"));

        Customer updatedCustomer = customerRepository.findById(testCustomer.getId()).get();
        assertThat(updatedCustomer.getAddresses()).hasSize(1);
        assertThat(updatedCustomer.getAddresses().get(0).getAddressTitle()).isEqualTo("Home");

    }

    @Test
    void testGetAllAddresses_WhenNoAddresses_ShouldReturn200OkEmptyList() throws Exception {

        mockMvc.perform(get("/api/customer/addresses")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAllAddresses_WhenAddressesExist_ShouldReturn200OkWithAddresses() throws Exception {

        AddressDto addressDto = new AddressDto();
        addressDto.setAddressTitle("Work");
        addressDto.setFullAddress("456 Work St, City, Country");

        mockMvc.perform(post("/api/customer/addresses")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addressDto)))
                        .andExpect(status().isCreated());


        mockMvc.perform(get("/api/customer/addresses")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].addressTitle").value("Work"));
    }

    @Test
    void testDeleteAddress_WhenValid_ShouldReturn200Ok() throws Exception {

        Address address = new Address();
        address.setCustomer(testCustomer);
        address.setAddressTitle("Will be Deleted");
        address.setFullAddress("789 Delete St, City, Country");
        address = addressRepository.save(address);
        testCustomer.getAddresses().add(address);

        Long addressIdToDelete = address.getId();

        assertThat(addressRepository.count()).isEqualTo(1);

        mockMvc.perform(delete("/api/customer/addresses/" + addressIdToDelete)
                    .header("Authorization", "Bearer " + customerToken))
                    .andExpect(status().isOk());

        assertThat(addressRepository.count()).isEqualTo(0);
    }

    @Test
    void testDeleteMyAddress_WhenAddressNotFound_ShouldReturn404NotFound() throws Exception {

        Long nonExistentAddressId = 9999L;


        mockMvc.perform(delete("/api/customer/addresses/" + nonExistentAddressId)
                        .header("Authorization", "Bearer " + customerToken))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.message").value("Address not found"));
    }

    @Test
    void testDeleteMyAddress_WhenNotOwner_ShouldReturn403Forbidden() throws Exception {

        User victimUser = new User();
        victimUser.setEmail("victim@gmail.com");
        victimUser.setPassword(passwordEncoder.encode("654321"));
        victimUser.setRole(Role.ROLE_CUSTOMER);
        victimUser.setVerified(true);
        userRepository.save(victimUser);

        Customer victimCustomer = new Customer();
        victimCustomer.setUser(victimUser);
        customerRepository.save(victimCustomer);

        Address victimAddress = new Address();
        victimAddress.setCustomer(victimCustomer);
        victimAddress.setAddressTitle("Victim's Address");
        victimAddress.setFullAddress("Victim St, City, Country");
        victimAddress = addressRepository.save(victimAddress);

        mockMvc.perform(delete("/api/customer/addresses/" + victimAddress.getId())
                        .header("Authorization", "Bearer " + customerToken))
                        .andExpect(status().isForbidden())
                        .andExpect(jsonPath("$.message").value("No permission to delete this address"));
    }

    @Test
    void testRegisterFcmToken_WhenValid_ShouldReturn200OK() throws Exception {

        String fcmToken = "c123...xyz:ABC...LMN"; // Örnek bir FCM token
        FcmTokenRequest requestDto = new FcmTokenRequest();
        requestDto.setToken(fcmToken);

        mockMvc.perform(post("/api/customer/register-fcm-token")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                        .andExpect(status().isOk());

        Customer updatedCustomer = customerRepository.findById(testCustomer.getId()).get();
        assertThat(updatedCustomer.getFcmToken()).isEqualTo(fcmToken);
    }
}
