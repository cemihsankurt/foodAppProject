package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.UserResponseDto;
import com.cemihsankurt.foodAppProject.entity.User;
import com.cemihsankurt.foodAppProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class
UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;


    @Override
    public boolean isEmailUnique(String email) {

        return userRepository.findByEmail(email).isEmpty();
    }
}
