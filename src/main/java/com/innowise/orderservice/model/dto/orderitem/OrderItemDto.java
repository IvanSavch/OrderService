package com.innowise.orderservice.model.dto.orderitem;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    @NotNull(message = "Item id not be null")
    private Long itemId;
    @NotNull(message = "Quantity not be null")
    @Min(value = 1,message = "Min quantity: 1")
    private int quantity;
}
