package com.app.data.dao.impl;

import com.app.data.dao.ClientDao;
import com.app.data.dao.repository.ClientRepository;
import com.app.data.entity.Client;
import com.app.exception.DBException;
import com.app.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ClientDaoImpl implements ClientDao {

    private ClientRepository clientRepository;

    @Autowired
    public ClientDaoImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client save(Client client) {
        Client createdClient;
        try {
            createdClient = this.clientRepository.save(client);
        } catch (DataIntegrityViolationException e) {
            log.error("Error when create client", e);
            throw new DBException("Such client is already exists");
        }
        return createdClient;
    }

    @Override
    public List<Client> findAll() {
        return (List<Client>) this.clientRepository.findAll();
    }

    @Override
    public Client findById(Long id) {
        return this.clientRepository.findById(id).orElseThrow(()
                -> new NotFoundException(String.format("Client with id '%s' is not found", id)));
    }

    @Override
    public void delete(Long id) {
        this.clientRepository.deleteById(id);
    }

    @Override
    public Client update(Client client) {
        Client updatedClient;
        try {
            updatedClient = this.clientRepository.save(client);
        } catch (DataIntegrityViolationException e) {
            log.error("Error when update client", e);
            throw new DBException("Error when update client");
        }
        return updatedClient;
    }
}
