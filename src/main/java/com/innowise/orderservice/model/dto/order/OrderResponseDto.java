package com.innowise.orderservice.model.dto.order;

import com.innowise.orderservice.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private String status;
    private BigDecimal totalPrice;
    private Boolean deleted;
    private UserDto userDto;
}
