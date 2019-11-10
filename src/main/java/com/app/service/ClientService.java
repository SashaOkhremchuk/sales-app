package com.app.service;

import com.app.data.entity.Client;

import java.util.List;

public interface ClientService {

    Client create(Client client);

    List<Client> findAll();

    Client findById(Long id);

    Client update(Client client);
}
