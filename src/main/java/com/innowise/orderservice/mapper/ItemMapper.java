package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.dto.item.ItemCreateDto;
import com.innowise.orderservice.model.dto.item.ItemResponseDto;
import com.innowise.orderservice.model.entity.Item;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toItem(ItemCreateDto itemCreateDto);
    ItemResponseDto toItemResponseDto(Item item);
    List<ItemResponseDto> toListItemResponseDto(List<Item> itemList);
}
