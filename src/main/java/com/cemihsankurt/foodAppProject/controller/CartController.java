package com.cemihsankurt.foodAppProject.controller;

import com.cemihsankurt.foodAppProject.dto.CartDto;
import com.cemihsankurt.foodAppProject.service.ICartService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController implements  ICartController {

    @Autowired
    private ICartService cartService;

    @Override
    @GetMapping
    public ResponseEntity<CartDto> getMyCart(Authentication authentication) {

        return ResponseEntity.ok(cartService.getCartContents(authentication));
    }

    @Override
    @PostMapping("/add")
    public ResponseEntity<CartDto> addProductToCart(@RequestBody AddToCartRequest addToCartRequest, Authentication authentication) {

        CartDto updatedCart = cartService.addToCart(addToCartRequest.getProductId(), addToCartRequest.getQuantity(), authentication);

        return ResponseEntity.ok(updatedCart);
    }

    @Override
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDto> removeProductFromCart(@PathVariable Long productId, Authentication authentication) {

        CartDto updatedCart = cartService.removeFromCart(productId,authentication);

        return ResponseEntity.ok(updatedCart);
    }

    @Data
    static class AddToCartRequest{
        private Long productId;
        private int quantity;
    }


}
