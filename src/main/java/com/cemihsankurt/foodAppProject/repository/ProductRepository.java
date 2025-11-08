package com.cemihsankurt.foodAppProject.repository;

import com.cemihsankurt.foodAppProject.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByRestaurantId(Long restaurantId);

}
