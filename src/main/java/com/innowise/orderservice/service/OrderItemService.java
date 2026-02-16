package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.orderitem.OrderItemDto;
import com.innowise.orderservice.model.entity.OrderItem;

public interface OrderItemService {
    OrderItem create(OrderItem orderItem);
    OrderItem findById(Long id);
    //public OrderItem updateById(Long id, OrderItemDto orderItemDto);
    void delete(OrderItem orderItem);
}
