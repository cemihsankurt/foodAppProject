package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.CartDto;
import com.cemihsankurt.foodAppProject.entity.Cart;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface ICartController {

    ResponseEntity<CartDto> getMyCart(Authentication authentication);

    ResponseEntity<CartDto> addProductToCart(CartController.AddToCartRequest addToCartRequest, Authentication authentication);

    ResponseEntity<CartDto> removeProductFromCart(Long productId, Authentication authentication);

}
