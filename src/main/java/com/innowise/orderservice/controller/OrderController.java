package com.innowise.orderservice.controller;


import com.innowise.orderservice.model.dto.order.OrderCreateDto;
import com.innowise.orderservice.model.dto.order.OrderResponseDto;
import com.innowise.orderservice.model.dto.order.OrderUpdateDto;
import com.innowise.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid OrderCreateDto orderCreateDto) {
        OrderResponseDto orderResponseDto = orderService.create(orderCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDto);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<OrderResponseDto>> getByUserId(@PathVariable Long id) {
        List<OrderResponseDto> byUserId = orderService.findByUserId(id);
        return ResponseEntity.ok(byUserId);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Long id) {
        OrderResponseDto byId = orderService.findById(id);
        return ResponseEntity.ok(byId);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAll(@RequestParam(required = false) String status,
                                                         @RequestParam(required = false) LocalDateTime from,
                                                         @RequestParam(required = false) LocalDateTime to,
                                                         @RequestParam(required = false, defaultValue = "0") int page) {
        List<OrderResponseDto> all = orderService.findAll(PageRequest.of(page, 20), status, from, to);
        return ResponseEntity.ok(all);

    }
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDto> updateById(@PathVariable Long id,
                                                       @RequestBody @Valid OrderUpdateDto orderUpdateDto) {
        OrderResponseDto orderResponseDto = orderService.updateById(id, orderUpdateDto);
        return ResponseEntity.ok(orderResponseDto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
