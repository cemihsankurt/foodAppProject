package com.cemihsankurt.foodAppProject.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemDto {

    private Long productId;

    private int quantity; //sepette bu üründen kaç tane var

    private String productName; //bu ürünün adı

    private BigDecimal unitPrice; //bir tanesinin fiyatı

    private BigDecimal lineTotalPrice; //bu ürün başına quantitysi ile unitPriceının carpımı


}
