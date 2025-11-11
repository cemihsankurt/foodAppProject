package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.CartDto;
import com.cemihsankurt.foodAppProject.dto.CartItemDto;
import com.cemihsankurt.foodAppProject.entity.*;
import com.cemihsankurt.foodAppProject.exception.ResourceNotFoundException;
import com.cemihsankurt.foodAppProject.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService implements ICartService {


    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CustomerRepository customerRepository;

    @Override
    public CartDto addToCart(Long productId, int quantity, Authentication authentication) {

        Cart myCart = findMyCart(authentication);
        Product product = productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product not found"));
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(myCart.getId(), productId);

        if(existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        }
        else {
            CartItem newItem = new CartItem();
            newItem.setCart(myCart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
            myCart.getCartItems().add(newItem);

        }

        return getCartContents(authentication);
    }

    @Override
    public CartDto removeFromCart(Long productId, Authentication authentication) {

        Cart myCart = findMyCart(authentication);
        CartItem itemToRemove = cartItemRepository.findByCartIdAndProductId(myCart.getId(), productId).orElseThrow(() -> new ResourceNotFoundException("Item not found in the cart"));
        cartItemRepository.delete(itemToRemove);
        myCart.getCartItems().remove(itemToRemove);

        return getCartContents(authentication);
    }

    @Override
    public CartDto getCartContents(Authentication authentication) {
        Cart myCart = findMyCart(authentication);

        return convertCartToDto(myCart);
    }



    private Cart findMyCart(Authentication authentication) {

        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return customer.getCart();
    }

    private CartDto convertCartToDto(Cart cart) {

        List<CartItemDto> itemDtos = new ArrayList<>();
        BigDecimal cartTotal = BigDecimal.ZERO;
        int totalItemCount = 0;

        for(CartItem cartItem : cart.getCartItems()) {

            Product product = cartItem.getProduct();
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            CartItemDto cartItemDto = CartItemDto.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .unitPrice(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .lineTotalPrice(lineTotal)
                    .build();

            itemDtos.add(cartItemDto);
            cartTotal = cartTotal.add(lineTotal);
            totalItemCount += cartItem.getQuantity();

        }

        return CartDto.builder()
                .items(itemDtos)
                .cartTotal(cartTotal)
                .totalItemCount(totalItemCount)
                .build();

    }
}
