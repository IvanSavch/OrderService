package com.innowise.orderservice.controller;

import com.innowise.orderservice.mapper.ItemMapper;
import com.innowise.orderservice.model.dto.item.ItemCreateDto;
import com.innowise.orderservice.model.dto.item.ItemResponseDto;
import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.service.ItemService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestBody ItemCreateDto itemCreateDto) {
        Item item = itemMapper.toItem(itemCreateDto);
        Item saved = itemService.create(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemMapper.toItemResponseDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> getById(@PathVariable Long id) {
        ItemResponseDto itemResponseDto = itemMapper.toItemResponseDto(itemService.findById(id));
        return ResponseEntity.ok(itemResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> findAll(@RequestParam(required = false) String name,
                                                         @RequestParam(required = false) BigDecimal price,
                                                         @RequestParam(required = false, defaultValue = "0") int page) {
        List<Item> all = itemService.findAll(PageRequest.of(page, 20), name, price);
        List<ItemResponseDto> listItemResponseDto = itemMapper.toListItemResponseDto(all);
        return ResponseEntity.ok(listItemResponseDto);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDto> updateById(@PathVariable Long id, ItemCreateDto itemCreateDto){
        Item updatedItem = itemService.updateById(id, itemCreateDto);
        ItemResponseDto itemResponseDto = itemMapper.toItemResponseDto(updatedItem);
        return ResponseEntity.ok(itemResponseDto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        Item byId = itemService.findById(id);
        itemService.delete(byId);
        return ResponseEntity.noContent().build();
    }
}
