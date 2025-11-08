package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.AddressDto;
import com.cemihsankurt.foodAppProject.entity.Customer;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ICustomerService {

    Long findCustomerIdByUserEmail(String email);

    Customer findCustomerByEmail(String email);

    void updateFcmToken(String userEmail, String token);

    AddressDto addAddress(AddressDto request, Authentication authentication);

    void deleteAddress(Long addressId, Authentication authentication);

    List<AddressDto> getMyAddresses(Authentication authentication);
}
