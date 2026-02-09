package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.OrderDto;
import com.innowise.orderservice.model.entity.Order;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    Order create(Order order);
    Order findById(Long id);
    List<Order> findByUserId(Long userId);
    List<Order> findAll(Pageable pageable, String status, LocalDateTime from, LocalDateTime to);
    Order updateById(Long id, OrderDto orderDto);
    void delete(Order order);

}
