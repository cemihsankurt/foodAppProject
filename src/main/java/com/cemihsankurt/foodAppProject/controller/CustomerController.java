package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.service.ICustomerService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
