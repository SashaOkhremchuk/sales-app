package com.app.data.dao.repository;

import com.app.data.entity.Client;
import com.app.data.entity.Item;
import com.app.data.entity.enums.Status;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {

    List<Item> findAllByStatus(Status status);

    List<Item> findAllBySellerAndStatus(Client client, Status status);
}
