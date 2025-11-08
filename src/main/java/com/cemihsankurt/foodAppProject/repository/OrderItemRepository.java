package com.cemihsankurt.foodAppProject.repository;

import com.cemihsankurt.foodAppProject.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
