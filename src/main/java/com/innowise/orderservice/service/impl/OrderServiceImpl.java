package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.exception.UserNotFoundException;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.dto.UserDto;
import com.innowise.orderservice.model.dto.order.OrderCreateDto;
import com.innowise.orderservice.model.dto.order.OrderResponseDto;
import com.innowise.orderservice.model.dto.orderitem.OrderItemDto;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.ItemService;
import com.innowise.orderservice.service.OrderService;
import com.innowise.orderservice.service.user.UserService;
import com.innowise.orderservice.specification.OrderSpecification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final UserService userService;
    private final ItemService itemService;


    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, UserService userService, ItemService itemService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public OrderResponseDto create(OrderCreateDto orderCreateDto) {
        UserDto userDto = userService.findByEmail(orderCreateDto.getEmail());

        Order order = new Order();
        order.setStatus(orderCreateDto.getStatus());
        order.setUserId(userDto.getId());
        order.setDeleted(false);

        BigDecimal totalPrice = new BigDecimal(0);

        List<OrderItem> orderItemList = new ArrayList<>();
        for (OrderItemDto orderItemDto:orderCreateDto.getOrderItemList()) {

            Item byId = itemService.findById(orderItemDto.getItemId());

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(byId);
            orderItem.setOrder(order);
            orderItem.setQuantity(orderItemDto.getQuantity());

            BigDecimal price = new BigDecimal(byId.getPrice().toString());
            BigDecimal quantity = new BigDecimal(orderItem.getQuantity());
            totalPrice = totalPrice.add(price.multiply(quantity));

            orderItemList.add(orderItem);
        }
        order.setTotalPrice(totalPrice);
        order.setList(orderItemList);
        Order save = orderRepository.save(order);

        return orderMapper.toResponse(save,userDto);
    }

    @Override
    public OrderResponseDto findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
        UserDto userDto = userService.findById(order.getUserId());
        return orderMapper.toResponse(order,userDto);
    }

    @Override
    public List<OrderResponseDto> findByUserId(Long userId) {
        UserDto userDto = userService.findById(userId);
        List<Order> byUserId = orderRepository.findByUserId(userId);
        if (byUserId.isEmpty()) {
            throw new OrderNotFoundException();
        }
        for (Order order: byUserId) {
            order.setUserId(userDto.getId());
        }

        return orderMapper.toListOrderResponseDto(byUserId,userDto);
    }
    @Override
    public List<OrderResponseDto> findAll(Pageable pageable, String status, LocalDateTime from,LocalDateTime to){
        Specification<Order> orderSpecification = Specification.allOf(OrderSpecification.hasStatus(status)).
                and(OrderSpecification.hasCreatedAt(from,to));
        List<Order> content = orderRepository.findAll(orderSpecification, pageable).getContent();
        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        for (Order order:content) {
            UserDto userDto = userService.findById(order.getUserId());
            OrderResponseDto response = orderMapper.toResponse(order, userDto);
            orderResponseDtoList.add(response);
        }
        return orderResponseDtoList ;
    }

    @Override
    @Transactional
    public Order updateById(Long id, OrderCreateDto orderCreateDto) {
        Order newOrder = orderMapper.toOrder(orderCreateDto);
        newOrder.setId(id);
        return orderRepository.save(newOrder);
    }

    @Override
    @Transactional
    public void delete(Order order) {
        orderRepository.delete(order);
    }

}
