package com.innowise.orderservice.model.dto.order;

import com.innowise.orderservice.model.dto.UserDto;
import com.innowise.orderservice.model.dto.orderitem.OrderItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private String status;
    private BigDecimal totalPrice;
    private Boolean deleted;
    private List<OrderItemResponseDto> items;
    private UserDto user;
}
