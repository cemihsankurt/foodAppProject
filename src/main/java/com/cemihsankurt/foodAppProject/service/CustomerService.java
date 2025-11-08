package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.entity.Customer;
import com.cemihsankurt.foodAppProject.entity.User;
import com.cemihsankurt.foodAppProject.exception.ResourceNotFoundException;
import com.cemihsankurt.foodAppProject.repository.CustomerRepository;
import com.cemihsankurt.foodAppProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService implements ICustomerService{

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Long findCustomerIdByUserEmail(String email) {

        return customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"))
                .getId();
    }

    @Override
    public Customer findCustomerByEmail(String email) {

        return customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    @Override
    public void updateFcmToken(String userEmail, String token) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        customer.setFcmToken(token);
        customerRepository.save(customer);
    }
}
