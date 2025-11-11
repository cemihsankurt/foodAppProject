package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.AddressDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ICustomerController {

    ResponseEntity<AddressDto> addAddress(AddressDto addressDto, Authentication authentication);

    ResponseEntity<Void> deleteAddress(Long id, Authentication authentication);

    ResponseEntity<List<AddressDto>> getAllAddresses(Authentication authentication);

}
