package com.innowise.orderservice.model.dto;

import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private Long orderId;
    private long itemId;
    private Long quantity;
}
