package com.innowise.orderservice.service;

import com.innowise.orderservice.model.entity.Item;

public interface ItemService {

    Item findById(Long id);
}

