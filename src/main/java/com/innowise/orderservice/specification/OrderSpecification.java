package com.innowise.orderservice.specification;

import com.innowise.orderservice.model.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {
    private OrderSpecification() {
    }

    public static Specification<Order> hasStatus(Order.OrderStatus status) {
        if (status == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Order> hasCreatedAt(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) {
            return null;

        }
        if (from != null && to == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from);
        }

        if (from == null && to != null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to);
        }


        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createdAt"), from, to);
    }
}
