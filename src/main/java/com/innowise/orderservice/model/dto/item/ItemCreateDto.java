package com.innowise.orderservice.model.dto.item;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateDto {
    @NotBlank(message = "Name not be null")
    @NotEmpty
    private String name;
    @DecimalMin(value = "0.01", message = "The price must be positive")
    private BigDecimal price;
}
