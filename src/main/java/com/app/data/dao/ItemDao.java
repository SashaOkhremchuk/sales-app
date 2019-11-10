package com.app.data.dao;

import com.app.data.entity.Client;
import com.app.data.entity.Item;

import java.util.List;

public interface ItemDao {

    Item save(Item item);

    List<Item> findAll();

    List<Item> findAllForSale();

    Item findById(Long id);

    void delete(Long id);

    Item update(Item item);

    List<Item> findAllSellingBySeller(Client client);
}
