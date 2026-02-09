package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.ItemDto;
import com.innowise.orderservice.model.dto.OrderItemDto;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.OrderItem;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrderItemService {
    OrderItem create(OrderItem orderItem);
    OrderItem findById(Long id);
    public OrderItem updateById(Long id, OrderItemDto orderItemDto);
    void delete(OrderItem orderItem);
}
