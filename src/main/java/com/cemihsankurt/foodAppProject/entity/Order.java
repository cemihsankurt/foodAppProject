package com.cemihsankurt.foodAppProject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "Orders")
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    private Long id;

    @ManyToMany
    private List<OrderItem> orderItems;


    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    private LocalDateTime orderTime = LocalDateTime.now();

    private BigDecimal totalPrice;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;




}
