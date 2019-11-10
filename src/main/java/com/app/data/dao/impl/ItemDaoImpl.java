package com.app.data.dao.impl;

import com.app.data.dao.ItemDao;
import com.app.data.dao.repository.ItemRepository;
import com.app.data.entity.Client;
import com.app.data.entity.Item;
import com.app.data.entity.enums.Status;
import com.app.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemDaoImpl implements ItemDao {

    private ItemRepository itemRepository;

    @Autowired
    public ItemDaoImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item save(Item item) {
        return this.itemRepository.save(item);
    }

    @Override
    public List<Item> findAll() {
        return (List<Item>) this.itemRepository.findAll();
    }

    @Override
    public List<Item> findAllForSale() {
        return this.itemRepository.findAllByStatus(Status.SELLING);
    }

    @Override
    public Item findById(Long id) {
        return this.itemRepository.findById(id).orElseThrow(()
                -> new NotFoundException(String.format("Item with id '%s' is not found", id)));
    }

    @Override
    public void delete(Long id) {
        this.itemRepository.deleteById(id);
    }

    @Override
    public Item update(Item item) {
        return this.itemRepository.save(item);
    }

    @Override
    public List<Item> findAllSellingBySeller(Client client) {
        return this.itemRepository.findAllBySellerAndStatus(client, Status.SELLING);
    }
}
