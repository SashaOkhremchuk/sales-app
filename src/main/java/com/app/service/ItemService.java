package com.app.service;

import com.app.data.entity.Item;

import java.util.List;

public interface ItemService {

    Item createItemForClient(Item item, Long clientId);

    void delete(Long id);

    List<Item> getAllForSale();

    Item findById(Long id);

    Item update(Item item, Long sellerId);

    List<Item> findAllSellingByOwnerId(Long clientId);
}
