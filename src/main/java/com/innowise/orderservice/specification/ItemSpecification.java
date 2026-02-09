package com.innowise.orderservice.specification;

import com.innowise.orderservice.model.entity.Item;
import com.innowise.orderservice.model.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ItemSpecification {
    private ItemSpecification() {
    }
    public static Specification<Item> hasName(String name) {
        if (name == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("name"), name);
    }
    public static Specification<Item> hasPrice(BigDecimal price) {
        if (price == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("price"), price);
    }
}
