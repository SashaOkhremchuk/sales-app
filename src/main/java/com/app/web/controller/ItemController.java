package com.app.web.controller;

import com.app.data.entity.Item;
import com.app.service.ItemService;
import com.app.web.dto.ItemDto;
import com.app.web.validation.ItemValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping(value = "/client/{clientId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> create(@RequestBody Item item, @PathVariable Long clientId) {
        ItemValidation.validate(item);
        this.itemService.createItemForClient(item, clientId);
        return new ResponseEntity<>(ItemDto.from(item), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        this.itemService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemDto>> getAllForSale() {
        List<ItemDto> items = new ArrayList<>();

        this.itemService.getAllForSale()
                .forEach(i -> items.add(ItemDto.from(i)));

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping(value = "/client/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemDto>> getAllSellingItemsByOwner(@PathVariable Long clientId) {
        List<ItemDto> items = new ArrayList<>();

        this.itemService.findAllSellingByOwnerId(clientId)
                .forEach(i -> items.add(ItemDto.from(i)));

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ItemDto> getById(@PathVariable Long id) {
        ItemDto itemDto = ItemDto.from(this.itemService.findById(id));
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @PutMapping(value = "/client/{clientId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Item> update(@RequestBody Item item, @PathVariable Long clientId) {
        ItemValidation.validate(item);
        return new ResponseEntity<>(this.itemService.update(item, clientId), HttpStatus.OK);
    }
}
