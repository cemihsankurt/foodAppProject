package com.cemihsankurt.foodAppProject.repository;

import com.cemihsankurt.foodAppProject.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
