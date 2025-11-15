package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.ProductDto;
import com.cemihsankurt.foodAppProject.dto.RestaurantDto;
import com.cemihsankurt.foodAppProject.dto.RestaurantPanelDto;
import com.cemihsankurt.foodAppProject.enums.ApprovalStatus;
import com.cemihsankurt.foodAppProject.entity.Product;
import com.cemihsankurt.foodAppProject.entity.Restaurant;
import com.cemihsankurt.foodAppProject.entity.User;
import com.cemihsankurt.foodAppProject.exception.ResourceNotFoundException;
import com.cemihsankurt.foodAppProject.repository.ProductRepository;
import com.cemihsankurt.foodAppProject.repository.RestaurantRepository;
import com.cemihsankurt.foodAppProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService implements IRestaurantService{

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public void updateAvailability(boolean isAvailable, Authentication authentication) {

       Restaurant restaurant = getCurrentRestaurant(authentication);

        if(restaurant.getApprovalStatus() != ApprovalStatus.APPROVED){
            throw new IllegalStateException("Only approved restaurants can update availability status.");
        }
        System.out.println("Before: " + restaurant.isAvailable());
        restaurant.setAvailable(isAvailable);
        System.out.println("After: " + restaurant.isAvailable());
        restaurantRepository.save(restaurant);

    }

    @Override
    public ProductDto addProductToMenu(ProductDto productDto, Authentication authentication) {

        Restaurant restaurant = getCurrentRestaurant(authentication);

        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setRestaurant(restaurant);

        Product savedProduct = productRepository.save(product);

        return convertToDto(savedProduct);


    }

    @Override
    public void deleteProductFromMenu(Long productId, Authentication authentication) {

        Restaurant restaurant = getCurrentRestaurant(authentication);

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if(!product.getRestaurant().getId().equals(restaurant.getId())){
            throw new AccessDeniedException("You have no permission to delete this product");
        }

        productRepository.delete(product);


    }

    @Override
    public ProductDto updateProduct(Long productId, ProductDto productDto, Authentication authentication) {

        Restaurant restaurant = getCurrentRestaurant(authentication);
        Product productToUpdate = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if(!productToUpdate.getRestaurant().getId().equals(restaurant.getId())){

            throw new AccessDeniedException("You have no permission to update this product");

        }

        productToUpdate.setName(productDto.getName());
        productToUpdate.setPrice(productDto.getPrice());
        productToUpdate.setDescription(productDto.getDescription());

        Product updatedProduct = productRepository.save(productToUpdate);

        return convertToDto(updatedProduct);
    }

    @Override
    public List<ProductDto> getMenuForRestaurant(Long restaurantId) {

        if(!restaurantRepository.existsById(restaurantId)){
            throw new ResourceNotFoundException("Restaurant not found");
        }

        List<Product> products = productRepository.findByRestaurantId(restaurantId);

        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantDto> getAvailableRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findByApprovalStatusAndIsAvailable(ApprovalStatus.APPROVED,true);
        return restaurants.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantPanelDto getMyPanelDetails(Authentication authentication) {

        Restaurant myRestaurant = getCurrentRestaurant(authentication);
        List<ProductDto> menuDto = myRestaurant.getMenu().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // 3. Her şeyi DTO'da birleştirip döndür
        return RestaurantPanelDto.builder()
                .restaurantId(myRestaurant.getId())
                .restaurantName(myRestaurant.getName())
                .isAvailable(myRestaurant.isAvailable())
                .approvalStatus(myRestaurant.getApprovalStatus())
                .menu(menuDto) // Menüyü de ekle
                .build();
    }

    private Restaurant getCurrentRestaurant(Authentication authentication) {

        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return restaurantRepository.findByUserId(user.getId()).orElseThrow(() -> new AccessDeniedException("This user does not have a restaurant"));
    }

    private ProductDto convertToDto(Product product) {

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }

    private RestaurantDto convertToDto(Restaurant restaurant) {

        return RestaurantDto.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .build();
    }
}
