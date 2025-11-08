package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.entity.Customer;

public interface ICustomerService {

    Long findCustomerIdByUserEmail(String email);

    Customer findCustomerByEmail(String email);

    void updateFcmToken(String userEmail, String token);
}
