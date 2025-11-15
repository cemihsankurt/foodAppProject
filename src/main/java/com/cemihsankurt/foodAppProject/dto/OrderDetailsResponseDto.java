package com.cemihsankurt.foodAppProject.dto;

import com.cemihsankurt.foodAppProject.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderDetailsResponseDto {

    private Long orderId;
    private String restaurantName;
    private String customerName;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;
    private List<OrderItemDto> orderItems;
    private LocalDateTime orderTime;
    private String deliveryAddress;

}
