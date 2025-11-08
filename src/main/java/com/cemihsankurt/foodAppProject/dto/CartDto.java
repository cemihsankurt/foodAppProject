package com.cemihsankurt.foodAppProject.dto;

import com.cemihsankurt.foodAppProject.entity.Customer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartDto {

    private List<CartItemDto> items;

    private int totalItemCount; //bütün quantitylerin toplamı örnek : 2 pizza + 3 ayran

    private BigDecimal cartTotal; //bütün cartitemlerın linetotallarının toplamı --> genel toplam fiyat


}
