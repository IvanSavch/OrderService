package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.exception.OrderNotFoundException;
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



    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, UserClient userClient, ItemService itemService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.userClient = userClient;
        this.itemService = itemService;

    }

    @Override
    public OrderResponseDto create(OrderCreateDto orderCreateDto) {
        UserDto userDto = userClient.findByEmail(orderCreateDto.getEmail());

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

           totalPrice = totalPrice.add(byId.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));

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
        UserDto userDto = userClient.findById(order.getUserId());
        return orderMapper.toResponse(order,userDto);
    }

    @Override
    public List<OrderResponseDto> findByUserId(Long userId) {
        UserDto userDto = userClient.findById(userId);
        List<Order> byUserId = orderRepository.findByUserId(userId);
        if (byUserId.isEmpty()) {
            throw new OrderNotFoundException();
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
            UserDto userDto = userClient.findById(order.getUserId());
            OrderResponseDto response = orderMapper.toResponse(order, userDto);
            orderResponseDtoList.add(response);
        }
        return orderResponseDtoList ;
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

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(itemDto.getQuantity());

            totalPrice = totalPrice.add(
                    item.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));

            order.getList().add(orderItem);
        }
        order.setTotalPrice(totalPrice);

        UserDto userDto = userClient.findById(order.getUserId());

        return orderMapper.toResponse(order, userDto);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
        orderRepository.delete(order);
    }
}
