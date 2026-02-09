package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.model.dto.OrderItemDto;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.repository.OrderItemRepository;
import com.innowise.orderservice.service.ItemService;
import com.innowise.orderservice.service.OrderItemService;
import com.innowise.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderItemImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderService orderService;
    private final ItemService itemService;

    public OrderItemImpl(OrderItemRepository orderItemRepository, OrderService orderService, ItemService itemService) {
        this.orderItemRepository = orderItemRepository;
        this.orderService = orderService;
        this.itemService = itemService;
    }

    @Override
    public OrderItem create(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public OrderItem findById(Long id) {
        return orderItemRepository.findById(id).orElseThrow(OrderNotFoundException::new);
    }

    @Override
    @Transactional
    public OrderItem updateById(Long id, OrderItemDto orderItemDto) {
        Order orderById = orderService.findById(orderItemDto.getOrderId());
        Item itemById = itemService.findById(orderItemDto.getItemId());
        return orderItemRepository.save(new OrderItem(id, orderById, itemById, orderItemDto.getQuantity()));
    }

    @Override
    @Transactional
    public void delete(OrderItem orderItem) {
        orderItemRepository.delete(orderItem);
    }
}
