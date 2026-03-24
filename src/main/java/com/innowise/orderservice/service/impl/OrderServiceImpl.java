package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.exception.OrderNotFoundException;
import com.innowise.orderservice.mapper.OrderItemMapper;
import com.innowise.orderservice.mapper.OrderMapper;
import com.innowise.orderservice.model.dto.UserDto;
import com.innowise.orderservice.model.dto.order.OrderCreateDto;
import com.innowise.orderservice.model.dto.order.OrderResponseDto;
import com.innowise.orderservice.model.dto.order.OrderUpdateDto;
import com.innowise.orderservice.model.dto.orderitem.OrderItemDto;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderItem;
import com.innowise.orderservice.repository.OrderRepository;
import com.innowise.orderservice.service.ItemService;
import com.innowise.orderservice.service.OrderService;
import com.innowise.orderservice.client.UserClient;
import com.innowise.orderservice.specification.OrderSpecification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final UserClient userClient;
    private final ItemService itemService;
    private final OrderItemMapper orderItemMapper;


    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, UserClient userClient, ItemService itemService, OrderItemMapper orderItemMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.userClient = userClient;
        this.itemService = itemService;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public OrderResponseDto create(OrderCreateDto orderCreateDto) {
        UserDto userDto = userClient.findByEmail(orderCreateDto.getEmail());

        Order order = orderMapper.toOrder(orderCreateDto);
        order.setStatus(Order.OrderStatus.CREATED);
        order.setUserId(userDto.getId());
        order.setDeleted(false);

        BigDecimal totalPrice = new BigDecimal(0);

        List<OrderItem> orderItemList = new ArrayList<>();
        for (OrderItemDto orderItemDto : orderCreateDto.getOrderItemList()) {

            Item byId = itemService.findById(orderItemDto.getItemId());

            OrderItem orderItem = orderItemMapper.toOrderItem(orderItemDto);
            orderItem.setItem(byId);
            orderItem.setOrder(order);

            totalPrice = totalPrice.add(byId.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));

            orderItemList.add(orderItem);
        }
        order.setTotalPrice(totalPrice);
        order.setList(orderItemList);
        Order save = orderRepository.save(order);
        return orderMapper.toResponse(save, userDto);
    }

    @Override
    public OrderResponseDto findById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
        UserDto userDto = userClient.findById(order.getUserId());
        return orderMapper.toResponse(order, userDto);
    }

    @Override
    public List<OrderResponseDto> findByUserId(Long userId) {
        UserDto userDto = userClient.findById(userId);
        List<Order> byUserId = orderRepository.findByUserId(userId);
        if (byUserId.isEmpty()) {
            throw new OrderNotFoundException();
        }
        return orderMapper.toListOrderResponseDto(byUserId, userDto);
    }

    @Override
    public List<OrderResponseDto> findAll(Pageable pageable,
                                          Order.OrderStatus status,
                                          LocalDateTime from,
                                          LocalDateTime to) {
        Specification<Order> orderSpecification = Specification
                .allOf(OrderSpecification.hasStatus(status)).
                and(OrderSpecification.hasCreatedAt(from, to)).and(OrderSpecification.hasDeleted());
        List<Order> content = orderRepository.findAll(orderSpecification, pageable).getContent();
        List<OrderResponseDto> orderResponseDtoList = new ArrayList<>();
        for (Order order : content) {
            UserDto userDto = userClient.findById(order.getUserId());
            OrderResponseDto response = orderMapper.toResponse(order, userDto);
            orderResponseDtoList.add(response);
        }
        return orderResponseDtoList;
    }

    @Override
    @Transactional
    public OrderResponseDto updateById(Long id, OrderUpdateDto orderUpdateDto) {
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
        order.setStatus(orderUpdateDto.getStatus());
        order.getList().clear();

        BigDecimal totalPrice = new BigDecimal(0);

        for (OrderItemDto itemDto : orderUpdateDto.getOrderItem()) {

            Item item = itemService.findById(itemDto.getItemId());

            OrderItem orderItem = orderItemMapper.toOrderItem(itemDto);
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(itemDto.getQuantity());
            totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));

            order.getList().add(orderItem);
        }
        order.setTotalPrice(totalPrice);

        UserDto userDto = userClient.findById(order.getUserId());
        orderRepository.save(order);
        return orderMapper.toResponse(order, userDto);
    }
    @Override
    public void updateStatusById(Long id, Order.OrderStatus status){
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
        order.setStatus(status);
        orderRepository.save(order);

    }
    @Override
    @Transactional
    public void deleteById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
        order.setDeleted(true);
        orderRepository.save(order);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 1 1 *")
    public void deletedRemarkedOrder() {
        orderRepository.deletedRemarkedOrder();
    }
}
