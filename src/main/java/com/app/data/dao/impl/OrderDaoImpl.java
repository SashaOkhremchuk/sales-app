package com.app.data.dao.impl;

import com.app.data.dao.OrderDao;
import com.app.data.dao.repository.OrderRepository;
import com.app.data.entity.Order;
import com.app.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDaoImpl implements OrderDao {

    private OrderRepository orderRepository;

    @Autowired
    public OrderDaoImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order save(Order order) {
        return this.orderRepository.save(order);
    }

    @Override
    public List<Order> findAll() {
        return (List<Order>) this.orderRepository.findAll();
    }

    @Override
    public List<Order> findAllByClient(Long id) {
        return this.orderRepository.findAllByClient_Id(id);
    }

    @Override
    public Order findById(Long id) {
        return this.orderRepository.findById(id).orElseThrow(()
                -> new NotFoundException(String.format("Order with id '%s' is not found", id)));
    }

    @Override
    public void delete(Long id) {
        this.orderRepository.deleteById(id);
    }

    @Override
    public Order update(Order order) {
        return this.orderRepository.save(order);
    }
}
