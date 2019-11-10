package com.app.service;

import com.app.data.entity.Order;

import java.util.List;

public interface OrderService {

    Order create(Order order, Long clientId, Long itemId);

    List<Order> findAllByClient(Long clientId);

    Order findById(Long id);

}
