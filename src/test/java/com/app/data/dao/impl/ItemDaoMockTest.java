package com.app.data.dao.impl;

import com.app.Application;
import com.app.data.dao.ItemDao;
import com.app.data.dao.repository.ItemRepository;
import com.app.data.entity.Client;
import com.app.data.entity.Item;
import com.app.data.entity.enums.Status;
import com.app.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
public class ItemDaoMockTest {

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
            .name("Laptop")
            .price(1222)
            .description("Grey laptop")
            .status(Status.SELLING)
            .seller(TEST_CLIENT)
            .build();

    private static final Item TEST_ITEM_EXPECTED = Item.builder()
            .id(TEST_ID)
            .name("Laptop")
            .price(1222)
            .description("Grey laptop")
            .status(Status.SELLING)
            .seller(TEST_CLIENT)
            .build();

    @Autowired
    private ItemDao itemDao;

    @MockBean
    private ItemRepository mockItemRepository;

    @Test
    void whenCreateItemShouldReturnItemWithIdTest() {
        //given
        when(mockItemRepository.save(TEST_ITEM)).thenReturn(TEST_ITEM_EXPECTED);
        //when
        Item actual = itemDao.save(TEST_ITEM);
        //then
        assertEquals(TEST_ITEM_EXPECTED, actual);
    }

    @Test
    void whenFindAllShouldReturnListOfItems() {
        //given
        when(mockItemRepository.findAll()).thenReturn(List.of(TEST_ITEM_EXPECTED));
        //when
        List<Item> actual = itemDao.findAll();
        //then
        assertEquals(List.of(TEST_ITEM_EXPECTED), actual);
    }

    @Test
    void whenFindAllForSaleShouldReturnListOfItemsWithSellingStatusTest() {
        //given
        when(mockItemRepository.findAllByStatus(Status.SELLING)).thenReturn(List.of(TEST_ITEM_EXPECTED));
        //when
        List<Item> actual = itemDao.findAllForSale();
        //then
        assertEquals(List.of(TEST_ITEM_EXPECTED), actual);
    }

    @Test
    void whenFindByIdShouldReturnItemTest() {
        //given
        when(mockItemRepository.findById(TEST_ID)).thenReturn(Optional.ofNullable(TEST_ITEM_EXPECTED));
        //when
        Item actual = itemDao.findById(TEST_ID);
        //then
        assertEquals(TEST_ITEM_EXPECTED, actual);
    }

    @Test
    void whenFindByIdReturnNullShouldThrowNotFoundExceptionTest() {
        //given
        when(mockItemRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        //when-then
        assertThrows(NotFoundException.class, () -> itemDao.findById(TEST_ID));
    }

    @Test
    void deleteTest() {
        //when
        itemDao.delete(TEST_ID);
        //then
        verify(mockItemRepository).deleteById(TEST_ID);
    }

    @Test
    void whenUpdateShouldReturnItemTest() {
        //given
        when(mockItemRepository.save(TEST_ITEM)).thenReturn(TEST_ITEM_EXPECTED);
        //when
        Item actual = itemDao.update(TEST_ITEM);
        //then
        assertEquals(TEST_ITEM_EXPECTED, actual);
    }

    @Test
    void whenFindAllSellingBySellerShouldReturnListOfItems() {
        //given
        when(mockItemRepository.findAllBySellerAndStatus(TEST_CLIENT, Status.SELLING)).thenReturn(List.of(TEST_ITEM_EXPECTED));
        //when
        List<Item> actual = itemDao.findAllSellingBySeller(TEST_CLIENT);
        //then
        assertEquals(List.of(TEST_ITEM_EXPECTED), actual);
    }
}
