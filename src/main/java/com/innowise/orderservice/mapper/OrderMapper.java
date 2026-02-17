package com.innowise.orderservice.mapper;

import com.innowise.orderservice.model.dto.UserDto;
import com.innowise.orderservice.model.dto.order.OrderResponseDto;
import com.innowise.orderservice.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(source = "order.id", target = "id")
    @Mapping(target = "items", source = "order.list")
    OrderResponseDto toResponse(Order order, UserDto userDto);

    default List<OrderResponseDto> toListOrderResponseDto(List<Order> orderList, UserDto userDto) {
        if (orderList == null) {
            return null;
        }
        List<OrderResponseDto> list = new ArrayList<>(orderList.size());
        for (Order order : orderList) {
            list.add(toResponse(order, userDto));
        }
        return list;
    }

}
