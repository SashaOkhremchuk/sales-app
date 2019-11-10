package com.app.web.controller;

import com.app.data.entity.Client;
import com.app.service.ClientService;
import com.app.web.validation.ClientValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Client> create(@RequestBody Client client) {
        ClientValidation.validate(client);
        return new ResponseEntity<>(this.clientService.create(client), HttpStatus.CREATED);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Client>> getAll() {
        return new ResponseEntity<>(this.clientService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Client> getById(@PathVariable Long id) {
        return new ResponseEntity<>(this.clientService.findById(id), HttpStatus.OK);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Client> update(@RequestBody Client client) {
        ClientValidation.validate(client);
        return new ResponseEntity<>(this.clientService.update(client), HttpStatus.OK);
    }
}
