package com.innowise.orderservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long userId;
    private String status;
    private Long totalPrice;
    private Boolean deleted;
}
