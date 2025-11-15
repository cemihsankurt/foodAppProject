package com.cemihsankurt.foodAppProject.config;

import com.cemihsankurt.foodAppProject.enums.Role;
import com.cemihsankurt.foodAppProject.entity.User;
import com.cemihsankurt.foodAppProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {

        if(userRepository.existsByRole(Role.ROLE_ADMIN)){

            System.out.println("Admin User is already exists.");
            return;
        }

        User admin = new User();
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setVerified(true);

        userRepository.save(admin);

        System.out.println("Admin User Successfully Created");
        System.out.println("Email : " + email);
    }
}
