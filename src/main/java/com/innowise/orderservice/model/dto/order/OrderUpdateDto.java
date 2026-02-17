package com.innowise.orderservice.model.dto.order;

import com.innowise.orderservice.model.dto.orderitem.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateDto {
    private String status;
    private Boolean deleted;
    private List<OrderItemDto> orderItem;
}
