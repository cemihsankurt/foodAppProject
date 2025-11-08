package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.CartDto;
import org.springframework.security.core.Authentication;

public interface ICartService {

    CartDto addToCart(Long productId, int quantity, Authentication authentication);

    CartDto getCartContents(Authentication authentication);

    CartDto removeFromCart(Long productId, Authentication authentication);
}
