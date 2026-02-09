package com.innowise.orderservice.service.impl;

import com.innowise.orderservice.exception.ItemNotFoundException;
import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.model.dto.ItemDto;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.repository.ItemRepository;
import com.innowise.orderservice.service.ItemService;
import com.innowise.orderservice.specification.ItemSpecification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemRepository itemRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public Item create(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    public List<Item> findAll(Pageable pageable, String name, BigDecimal price) {
        Specification<Item> itemSpecification = Specification.allOf(ItemSpecification.hasName(name).
                and(ItemSpecification.hasPrice(price)));

        return itemRepository.findAll(itemSpecification,pageable).getContent();
    }

    @Override
    @Transactional
    public Item updateById(Long id, ItemDto itemDto) {
        Item newItem = itemMapper.toItem(itemDto);
        newItem.setId(id);
        return itemRepository.save(newItem);
    }

    @Override
    @Transactional
    public void delete(Item item) {
        itemRepository.delete(item);
    }
}
