package com.app.service.impl;

import com.app.data.dao.ClientDao;
import com.app.data.dao.ItemDao;
import com.app.data.entity.Client;
import com.app.data.entity.Item;
import com.app.data.entity.enums.Status;
import com.app.exception.DBException;
import com.app.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private ItemDao itemDao;
    private ClientDao clientDao;

    @Autowired
    public ItemServiceImpl(ItemDao itemDao, ClientDao clientDao) {
        this.itemDao = itemDao;
        this.clientDao = clientDao;
    }

    @Override
    public Item createItemForClient(Item item, Long clientId) {
        Client client = this.clientDao.findById(clientId);
        item.setSeller(client);
        item.setStatus(Status.SELLING);
        client.getSellingItems().add(item);
        this.clientDao.update(client);
        return item;
    }

    @Override
    public void delete(Long id) {
        Item item = this.itemDao.findById(id);
        checkIfItemIsSelling(item.getStatus(), item.getId());
        this.itemDao.delete(id);
    }

    @Override
    public List<Item> getAllForSale() {
        return this.itemDao.findAllForSale();
    }

    @Override
    public Item findById(Long id) {
        return this.itemDao.findById(id);
    }

    @Override
    public Item update(Item item, Long clientId) {
        Status status = this.itemDao.findById(item.getId()).getStatus();

        checkIfItemIsSelling(status, item.getId());

        Client seller = this.clientDao.findById(clientId);
        item.setStatus(status);
        item.setSeller(seller);
        seller.getSellingItems().add(item);
        this.itemDao.update(item);

        return item;
    }

    @Override
    public List<Item> findAllSellingByOwnerId(Long clientId) {
        Client client = this.clientDao.findById(clientId);
        return this.itemDao.findAllSellingBySeller(client);
    }

    private void checkIfItemIsSelling(Status itemStatus, Long itemId) {
        if (itemStatus.equals(Status.SOLD)) {
            throw new DBException(String.format("Item with id: '%s' is already sold", itemId));
        }
    }
}
