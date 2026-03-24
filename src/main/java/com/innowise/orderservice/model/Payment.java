package com.innowise.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String id;
    private Long orderId;
    private String status;
    public enum Status {
        SUCCESS, FAILED
    }
}
