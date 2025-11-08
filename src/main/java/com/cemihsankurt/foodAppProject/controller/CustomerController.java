package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.AddressDto;
import com.cemihsankurt.foodAppProject.service.ICustomerService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
class FcmTokenRequest{
    private String token;
}

@RestController
@RequestMapping("/api/customer")
public class CustomerController implements ICustomerController{

    @Autowired
    private ICustomerService customerService;

    @PostMapping("/register-fcm-token")
    public ResponseEntity<Void> registerFcmToken(@RequestBody FcmTokenRequest request, Authentication authentication) {
        customerService.updateFcmToken(authentication.getName(), request.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addresses")
    @Override
    public ResponseEntity<AddressDto> addAddress(@Valid @RequestBody AddressDto addressDto, Authentication authentication) {
        AddressDto newAddress = customerService.addAddress(addressDto, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAddress);
    }

    @DeleteMapping("/addresses/{addressId}")
    @Override
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId, Authentication authentication) {
        customerService.deleteAddress(addressId, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/addresses")
    @Override
    public ResponseEntity<List<AddressDto>> getAllAddresses(Authentication authentication) {

        List<AddressDto> addresses = customerService.getMyAddresses(authentication);
        return ResponseEntity.ok().body(addresses);
    }
}
