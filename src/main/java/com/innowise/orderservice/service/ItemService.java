package com.innowise.orderservice.service;

import com.innowise.orderservice.model.dto.ItemDto;
import com.innowise.orderservice.model.entity.Item;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ItemService {
    Item create(Item item);

    Item findById(Long id);

    List<Item> findAll(Pageable pageable, String name, BigDecimal price);

    Item updateById(Long id, ItemDto itemDto);

    void delete(Item item);
}
