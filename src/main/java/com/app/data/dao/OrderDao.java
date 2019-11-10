package com.app.data.dao;

import com.app.data.entity.Order;

import java.util.List;

public interface OrderDao {

    Order save(Order order);

    List<Order> findAll();

    List<Order> findAllByClient(Long id);

    Order findById(Long id);

    void delete(Long id);

    Order update(Order order);

}
