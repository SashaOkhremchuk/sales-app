package com.app.service.impl;

import com.app.data.dao.ClientDao;
import com.app.data.entity.Client;
import com.app.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private ClientDao clientDao;

    @Autowired
    public ClientServiceImpl(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @Override
    public Client create(Client client) {
        return this.clientDao.save(client);
    }

    @Override
    public List<Client> findAll() {
        return this.clientDao.findAll();
    }

    @Override
    public Client findById(Long id) {
        return this.clientDao.findById(id);
    }

    @Override
    public Client update(Client client) {
        return this.clientDao.update(client);
    }
}
