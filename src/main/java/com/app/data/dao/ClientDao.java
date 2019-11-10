package com.app.data.dao;

import com.app.data.entity.Client;

import java.util.List;

public interface ClientDao {

    Client save(Client client);

    List<Client> findAll();

    Client findById(Long id);

    void delete(Long id);

    Client update(Client client);
}
