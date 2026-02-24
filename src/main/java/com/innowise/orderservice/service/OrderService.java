package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.order.OrderCreateDto;
import com.innowise.orderservice.model.dto.order.OrderResponseDto;
import com.innowise.orderservice.model.dto.order.OrderUpdateDto;
import com.innowise.orderservice.model.entity.Order;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    OrderResponseDto create(OrderCreateDto orderCreateDto);
    OrderResponseDto findById(Long id);
    List<OrderResponseDto> findByUserId(Long userId);
    List<OrderResponseDto> findAll(Pageable pageable,
                                   Order.OrderStatus status,
                                   LocalDateTime from,
                                   LocalDateTime to);
    OrderResponseDto updateById(Long id, OrderUpdateDto orderUpdateDto);
    void deleteById(Long id);
}
