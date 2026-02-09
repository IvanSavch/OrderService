package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.dto.OrderDto;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.OrderService;
import com.innowise.orderservice.specification.OrderSpecification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Override
    public Order create(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        List<Order> byUserId = orderRepository.findByUserId(userId);
        if (byUserId.isEmpty()) {
            throw new OrderNotFoundException();
        }
        return byUserId;
    }
    @Override
    public List<Order> findAll(Pageable pageable, String status, LocalDateTime from,LocalDateTime to){
        Specification<Order> orderSpecification = Specification.allOf(OrderSpecification.hasStatus(status).
                and(OrderSpecification.hasCreatedAt(from,to)));
        return orderRepository.findAll(orderSpecification,pageable).getContent();
    }

    @Override
    @Transactional
    public Order updateById(Long id, OrderDto orderDto) {
        Order newOrder = orderMapper.toOrder(orderDto);
        newOrder.setId(id);
        return orderRepository.save(newOrder);
    }

    @Override
    @Transactional
    public void delete(Order order) {
        orderRepository.delete(order);
    }

}
