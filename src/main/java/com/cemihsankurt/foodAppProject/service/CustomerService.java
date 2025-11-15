package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.AddressDto;
import com.cemihsankurt.foodAppProject.entity.Address;
import com.cemihsankurt.foodAppProject.entity.Customer;
import com.cemihsankurt.foodAppProject.entity.User;
import com.cemihsankurt.foodAppProject.exception.ResourceNotFoundException;
import com.cemihsankurt.foodAppProject.repository.AddressRepository;
import com.cemihsankurt.foodAppProject.repository.CustomerRepository;
import com.cemihsankurt.foodAppProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService implements ICustomerService{

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public Long findCustomerIdByUserEmail(String email) {

        Customer customer  = customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return customer.getId();
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

    @Override
    public AddressDto addAddress(AddressDto request, Authentication authentication) {

        Customer myCustomer = getCurrentCustomer(authentication);
        Address address = new Address();
        address.setCustomer(myCustomer);
        address.setAddressTitle(request.getAddressTitle());
        address.setFullAddress(request.getFullAddress());
        Address savedAddress = addressRepository.save(address);
        myCustomer.getAddresses().add(savedAddress);

        return convertToDto(savedAddress);
    }

    @Override
    public void deleteAddress(Long addressId, Authentication authentication) {

        Customer myCustomer = getCurrentCustomer(authentication);
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if(!address.getCustomer().getId().equals(myCustomer.getId())) {
            throw new AccessDeniedException("No permission to delete this address");
        }
        myCustomer.getAddresses().remove(address);
        addressRepository.delete(address);
    }

    @Override
    public List<AddressDto> getMyAddresses(Authentication authentication) {

        Customer myCustomer = getCurrentCustomer(authentication);

        return myCustomer.getAddresses().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private Customer getCurrentCustomer(Authentication authentication) {

        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return customerRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    private AddressDto convertToDto(Address address) {
        return AddressDto.builder()
                .id(address.getId())
                .addressTitle(address.getAddressTitle())
                .fullAddress(address.getFullAddress())
                .build();
    }
}
