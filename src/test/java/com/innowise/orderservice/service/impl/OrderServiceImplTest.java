package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.client.UserClient;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderItemMapper orderItemMapper;
    @Mock
    private UserClient userClient;
    @Mock
    private ItemService itemService;
    @InjectMocks
    private OrderServiceImpl orderService;


    @Test
    void create() {
        OrderCreateDto createDto = new OrderCreateDto();
        createDto.setEmail("test@mail.com");

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setItemId(1L);
        itemDto.setQuantity(2);

        createDto.setOrderItemList(List.of(itemDto));

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setPrice(new BigDecimal(100));

        Order savedOrder = new Order();
        savedOrder.setId(1L);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);

        savedOrder.setList(List.of(orderItem));

        when(userClient.findByEmail("test@mail.com")).thenReturn(userDto);
        when(itemService.findById(1L)).thenReturn(item);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderMapper.toOrder(any(OrderCreateDto.class))).thenReturn(savedOrder);
        when(orderItemMapper.toOrderItem(any(OrderItemDto.class))).thenReturn(orderItem);
        when(orderMapper.toResponse(any(Order.class), eq(userDto))).thenReturn(new OrderResponseDto());


        OrderResponseDto result = orderService.create(createDto);

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
        verify(userClient).findByEmail("test@mail.com");
    }

    @Test
    void findById() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setUserId(userDto.getId());

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userClient.findById(1L)).thenReturn(userDto);
        when(orderMapper.toResponse(order, userDto)).thenReturn(new OrderResponseDto());

        OrderResponseDto result = orderService.findById(1L);
        assertNotNull(result);
    }

    @Test
    void findByIdOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.findById(1L));
    }

    @Test
    void findByUserId() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        Order order1 = new Order();
        order1.setId(1L);
        order1.setUserId(userDto.getId());

        Order order2 = new Order();
        order2.setId(2L);
        order2.setUserId(userDto.getId());
        List<Order> orderList = List.of(order1, order2);

        when(userClient.findById(1L)).thenReturn(userDto);
        when(orderRepository.findByUserId(1L)).thenReturn(orderList);
        when(orderMapper.toListOrderResponseDto(orderList, userDto))
                .thenReturn(List.of(new OrderResponseDto(), new OrderResponseDto()));

        List<OrderResponseDto> result = orderService.findByUserId(1L);

        assertEquals(2, result.size());
        verify(orderRepository).findByUserId(1L);
        verify(userClient).findById(1L);
    }

    @Test
    void findByUserIdThrowOrderNotFound() {
        Long userId = 2L;

        when(userClient.findById(userId)).thenReturn(new UserDto());
        when(orderRepository.findByUserId(userId)).thenReturn(List.of());

        assertThrows(OrderNotFoundException.class, () -> orderService.findByUserId(userId));
    }

    @Test
    void findAll() {
        Pageable pageable = mock(Pageable.class);

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        Order order = new Order();
        order.setUserId(userDto.getId());
        order.setStatus(Order.OrderStatus.CREATED);
        Order order2 = new Order();
        order2.setUserId(userDto.getId());
        order2.setStatus(Order.OrderStatus.CREATED);

        List<Order> orders = List.of(order, order2);
        Page<Order> page = new PageImpl<>(orders);

        when(orderRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(userClient.findById(1L)).thenReturn(userDto);
        when(orderMapper.toResponse(order, userDto)).thenReturn(new OrderResponseDto());

        List<OrderResponseDto> result = orderService.findAll(pageable, Order.OrderStatus.CREATED, null, null);

        assertEquals(2, result.size());
        verify(orderRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void updateById() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setPrice(new BigDecimal("100"));

        Order order = new Order();
        order.setId(1L);
        order.setUserId(userDto.getId());
        order.setList(new java.util.ArrayList<>());

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setItem(item);
        OrderUpdateDto updateDto = new OrderUpdateDto();
        updateDto.setStatus(Order.OrderStatus.DELIVERED);

        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setItemId(item.getId());
        itemDto.setQuantity(3);

        updateDto.setOrderItem(List.of(itemDto));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(itemService.findById(1L)).thenReturn(item);
        when(userClient.findById(1L)).thenReturn(userDto);
        when(orderItemMapper.toOrderItem(any(OrderItemDto.class))).thenReturn(orderItem);
        when(orderMapper.toResponse(order, userDto)).thenReturn(new OrderResponseDto());

        OrderResponseDto orderResponseDto = orderService.updateById(1L, updateDto);

        assertEquals(Order.OrderStatus.DELIVERED, order.getStatus());
        assertEquals(new BigDecimal("300"), order.getTotalPrice());
        assertNotNull(orderResponseDto);
    }

    @Test
    void deleteById() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.deleteById(1L);

        assertTrue(order.getDeleted());
        verify(orderRepository).save(order);
    }

    @Test
    void deleteByIdOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteById(1L));
    }
}