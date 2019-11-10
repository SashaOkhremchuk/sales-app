package com.app.data.dao.impl;

import com.app.Application;
import com.app.data.dao.OrderDao;
import com.app.data.dao.repository.OrderRepository;
import com.app.data.entity.Client;
import com.app.data.entity.Item;
import com.app.data.entity.Order;
import com.app.data.entity.enums.Payment;
import com.app.data.entity.enums.Status;
import com.app.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
public class OrderDaoMockTest {

    private static final long TEST_ID = 1L;

    private static final Client TEST_CLIENT = Client.builder()
            .id(TEST_ID)
            .firstName("John")
            .lastName("Cena")
            .email("cenajohn@gmail.com")
            .phoneNumber("0981727477")
            .address("West Newbury")
            .build();

    private static final Item TEST_ITEM = Item.builder()
            .id(TEST_ID)
            .name("Laptop")
            .price(1222)
            .description("Grey laptop")
            .status(Status.SELLING)
            .seller(TEST_CLIENT)
            .build();

    private static final Order TEST_ORDER = Order.builder()
            .paymentMethod(Payment.CASH)
            .date(Date.valueOf("2019-10-25"))
            .client(TEST_CLIENT)
            .item(TEST_ITEM)
            .build();

    private static final Order TEST_ORDER_EXPECTED = Order.builder()
            .id(TEST_ID)
            .paymentMethod(Payment.CASH)
            .date(Date.valueOf("2019-10-25"))
            .client(TEST_CLIENT)
            .item(TEST_ITEM)
            .build();

    @Autowired
    private OrderDao orderDao;

    @MockBean
    private OrderRepository mockOrderRepository;

    @Test
    void whenCreateOrderShouldReturnOrderWithIdTest() {
        //given
        when(mockOrderRepository.save(TEST_ORDER)).thenReturn(TEST_ORDER_EXPECTED);
        //when
        Order actual = orderDao.save(TEST_ORDER);
        //then
        assertEquals(TEST_ORDER_EXPECTED, actual);
    }

    @Test
    void whenFindAllShouldReturnListOfOrders() {
        //given
        when(mockOrderRepository.findAll()).thenReturn(List.of(TEST_ORDER_EXPECTED));
        //when
        List<Order> actual = orderDao.findAll();
        //then
        assertEquals(List.of(TEST_ORDER_EXPECTED), actual);
    }

    @Test
    void whenFindAllByClientShouldReturnListOfOrders() {
        //given
        when(mockOrderRepository.findAllByClient_Id(TEST_ID)).thenReturn(List.of(TEST_ORDER_EXPECTED));
        //when
        List<Order> actual = orderDao.findAllByClient(TEST_ID);
        //then
        assertEquals(List.of(TEST_ORDER_EXPECTED), actual);
    }

    @Test
    void whenFindByIdShouldReturnOrderTest() {
        //given
        when(mockOrderRepository.findById(TEST_ID)).thenReturn(Optional.ofNullable(TEST_ORDER_EXPECTED));
        //when
        Order actual = orderDao.findById(TEST_ID);
        //then
        assertEquals(TEST_ORDER_EXPECTED, actual);
    }

    @Test
    void whenFindByIdReturnNullShouldThrowNotFoundExceptionTest() {
        //given
        when(mockOrderRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        //when-then
        assertThrows(NotFoundException.class, () -> orderDao.findById(TEST_ID));
    }

    @Test
    void deleteTest() {
        //when
        orderDao.delete(TEST_ID);
        //then
        verify(mockOrderRepository).deleteById(TEST_ID);
    }

    @Test
    void whenUpdateShouldReturnItemTest() {
        //given
        when(mockOrderRepository.save(TEST_ORDER)).thenReturn(TEST_ORDER_EXPECTED);
        //when
        Order actual = orderDao.update(TEST_ORDER);
        //then
        assertEquals(TEST_ORDER_EXPECTED, actual);
    }
}
