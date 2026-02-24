package com.innowise.orderservice.model.dto.order;

import com.innowise.orderservice.model.dto.orderitem.OrderItemDto;
import com.innowise.orderservice.model.entity.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateDto {
    @NotNull(message = "Status not be null")
    private Order.OrderStatus status;
    @Valid
    @NotNull(message = "OrderItem not be null")
    private List<OrderItemDto> orderItem;
}
