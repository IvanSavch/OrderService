package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.dto.orderitem.OrderItemResponseDto;
import com.innowise.orderservice.model.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "price", source = "item.price")
    OrderItemResponseDto toOrderItemResponseDto(OrderItem orderItem);
    List<OrderItemResponseDto> toListOrderItemResponseDTO(List<OrderItem> list);
}
