package com.app.web.controller;

import com.app.data.entity.Order;
import com.app.service.OrderService;
import com.app.web.dto.OrderDto;
import com.app.web.validation.OrderValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping(value = "/client/{clientId}/item/{itemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDto> create(@RequestBody Order order, @PathVariable Long clientId,
                                           @PathVariable Long itemId) {
        OrderValidation.validate(order);
        this.orderService.create(order, clientId, itemId);
        return new ResponseEntity<>(OrderDto.from(order), HttpStatus.CREATED);
    }

    @GetMapping(value = "/client/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderDto>> getAllByClient(@PathVariable Long clientId) {
        List<OrderDto> orders = new ArrayList<>();

        this.orderService.findAllByClient(clientId)
                .forEach(o -> orders.add(OrderDto.from(o)));

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        OrderDto orderDto = OrderDto.from(this.orderService.findById(id));
        return new ResponseEntity<>(orderDto, HttpStatus.OK);
    }
}
