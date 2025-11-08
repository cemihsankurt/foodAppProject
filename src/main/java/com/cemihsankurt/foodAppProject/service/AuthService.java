package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.*;
import com.cemihsankurt.foodAppProject.entity.*;
import com.cemihsankurt.foodAppProject.exception.ResourceNotFoundException;
import com.cemihsankurt.foodAppProject.repository.CustomerRepository;
import com.cemihsankurt.foodAppProject.repository.RestaurantRepository;
import com.cemihsankurt.foodAppProject.repository.UserRepository;
import com.cemihsankurt.foodAppProject.repository.VerificationRepository;
import com.cemihsankurt.foodAppProject.security.JwtTokenProvider;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private IEmailService emailService;



    @Override
    public UserResponseDto registerRestaurant(RegisterRestaurantRequestDto registerRestaurantRequestDto) {

        if(userRepository.existsByEmail(registerRestaurantRequestDto.getEmail())) {
            throw new UsernameNotFoundException("Email Already Exists");
        }

        UserResponseDto userResponseDto = new UserResponseDto();
        User user = new  User();

        user.setEmail(registerRestaurantRequestDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(registerRestaurantRequestDto.getPassword()));
        user.setRole(Role.ROLE_RESTAURANT);
        user.setVerified(false);
        User savedUser = userRepository.save(user);

        Restaurant restaurant = new Restaurant();
        restaurant.setName(registerRestaurantRequestDto.getRestaurantName());
        restaurant.setPhoneNumber(registerRestaurantRequestDto.getPhoneNumber());
        restaurant.setUser(savedUser);

        restaurantRepository.save(restaurant);


        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token,savedUser);
        verificationRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + token;
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationLink);


        userResponseDto.setEmail(savedUser.getEmail());
        return userResponseDto;
    }

    @Override
    public UserResponseDto registerCustomer(RegisterCustomerRequestDto registerCustomerRequestDto) {

        if(userRepository.existsByEmail(registerCustomerRequestDto.getEmail())) {
            throw new ResourceNotFoundException("Email Already Exists");
        }

        UserResponseDto userResponseDto = new UserResponseDto();
        User user = new  User();

        user.setEmail(registerCustomerRequestDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(registerCustomerRequestDto.getPassword()));
        user.setRole(Role.ROLE_CUSTOMER);
        user.setVerified(false);
        User savedUser = userRepository.save(user);

        Cart newCart = new Cart();

        Customer customer = Customer.builder().
                firstName(registerCustomerRequestDto.getFirstName())
                .lastName(registerCustomerRequestDto.getLastName())
                .phoneNumber(registerCustomerRequestDto.getPhoneNumber())
                .user(savedUser)
                .cart(newCart)
                .build();

        newCart.setCustomer(customer);

        customerRepository.save(customer);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token,savedUser);
        verificationRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + token;
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationLink);




        userResponseDto.setEmail(savedUser.getEmail());
        return userResponseDto;
    }

    @Override
    public AuthResponseDto authenticate(LoginRequestDto loginRequestDto) {


        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                );
        authenticationProvider.authenticate(authenticationToken);
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User authenticated but not found in DB"));

        if (!user.isVerified()) {
            throw new AccessDeniedException("User not verified, please verify it first to login");
        }

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );


        String generatedToken = jwtTokenProvider.generateToken(userDetails);
        return new AuthResponseDto(generatedToken);


    }

    public String verifyToken(String token){

        Optional<VerificationToken> verificationToken = verificationRepository.findByToken(token);
        if(verificationToken.isEmpty()){
            return "Invalid Link";
        }
        VerificationToken vToken = verificationToken.get();
        if(vToken.getExpiryDate().isBefore(LocalDateTime.now())){
            verificationRepository.delete(vToken);
            return "Link Expired";
        }
        User user = vToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        verificationRepository.delete(vToken);
        return "Email Verified Successfully";

    }



}
