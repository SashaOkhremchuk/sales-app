package com.app.service.impl;

import com.app.data.dao.ClientDao;
import com.app.data.dao.ItemDao;
import com.app.data.dao.OrderDao;
import com.app.data.entity.Client;
import com.app.data.entity.Item;
import com.app.data.entity.Order;
import com.app.data.entity.enums.Status;
import com.app.exception.DBException;
import com.app.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private OrderDao orderDao;
    private ClientDao clientDao;
    private ItemDao itemDao;

    @Autowired
    public OrderServiceImpl(OrderDao orderDao, ClientDao clientDao, ItemDao itemDao) {
        this.orderDao = orderDao;
        this.clientDao = clientDao;
        this.itemDao = itemDao;
    }

    @Override
    public Order create(Order order, Long clientId, Long itemId) {
        Client customer = this.clientDao.findById(clientId);
        Item item = this.itemDao.findById(itemId);
        Client seller = item.getSeller();

        checkIfOrderCanBeDone(seller, customer, item);

        customer.getOrders().add(order);
        order.setClient(customer);
        item.setStatus(Status.SOLD);
        order.setItem(item);
        order.setDate(new Date(System.currentTimeMillis()));
        this.clientDao.update(customer);

        return order;
    }

    @Override
    public List<Order> findAllByClient(Long clientId) {
        return this.orderDao.findAllByClient(clientId);
    }

    @Override
    public Order findById(Long id) {
        return this.orderDao.findById(id);
    }

    private void checkIfOrderCanBeDone(Client seller, Client customer, Item item) {
        if (seller.equals(customer) || item.getStatus().equals(Status.SOLD)) {
            throw new DBException("This order can not be done");
        }
    }
}
