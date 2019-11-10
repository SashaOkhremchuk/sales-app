package com.app.web.controller.integration;

import com.app.data.dao.OrderDao;
import com.app.data.entity.Client;
import com.app.data.entity.Item;
import com.app.data.entity.Order;
import com.app.data.entity.enums.Payment;
import com.app.data.entity.enums.Status;
import com.app.web.controller.ControllerBaseTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderControllerIntegrationTest extends ControllerBaseTest {

    @Autowired
    private OrderDao orderDao;

    private static final long TEST_ID = 1L;
    private static final long TEST_CLIENT_ID = 2L;

    private static final Client TEST_CLIENT = Client.builder()
            .id(TEST_CLIENT_ID)
            .firstName("Kendrick")
            .lastName("Lamar")
            .email("kendrickL@gmail.com")
            .phoneNumber("0941727477")
            .address("West Newbury")
            .build();

    private static final Client TEST_SELLER = Client.builder()
            .firstName("John")
            .lastName("Cena")
            .email("cenajohn@gmail.com")
            .phoneNumber("0998877654")
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
            .build();

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')"),
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (2, 'Kendrick', 'Lamar', 'kendrickL@gmail.com', '0941727477', 'West Newbury')"),
            @Sql(statements = "INSERT INTO item" +
                    " (id, name, price, description, status, seller_id)" +
                    " VALUES (1, 'Laptop', 1222, 'Grey laptop', 'SELLING', 1)")
    })
    void whenCreateOrderShouldRespondCreatedTest() throws Exception {
        //when-then
        mockMvc.perform(post(String.format("/orders/client/%d/item/%d", TEST_CLIENT_ID, TEST_ID))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_ORDER)))
                .andExpect(status().isCreated());//assert

        List<Order> allOrders = orderDao.findAll();
        Order actual = allOrders.get(0);

        //assert
        assertEquals(1, allOrders.size());
        assertEquals(TEST_ORDER.getPaymentMethod(), actual.getPaymentMethod());
        assertNotNull(actual.getDate());
        assertEquals(TEST_ITEM, actual.getItem());
        assertEquals(TEST_CLIENT, actual.getClient());

        assertTrue(Objects.nonNull(actual.getId()) && actual.getId() > 0);
    }

    @Test
    void whenCreateOrderByNonExistentClientOrItemShouldRespondNotFoundTest() throws Exception {
        //when-then
        mockMvc.perform(post(String.format("/orders/client/%d/item/%d", TEST_CLIENT_ID, TEST_ID))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_ORDER)))
                //assert
                .andExpect(status().isNotFound());
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')"),
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (2, 'Kendrick', 'Lamar', 'kendrickL@gmail.com', '0941727477', 'West Newbury')"),
            @Sql(statements = "INSERT INTO item" +
                    " (id, name, price, description, status, seller_id)" +
                    " VALUES (1, 'Laptop', 1222, 'Grey laptop', 'SOLD', 1)")
    })
    void whenCreateOrderToSoldItemShouldRespondConflictTest() throws Exception {
        //when-then
        mockMvc.perform(post(String.format("/orders/client/%d/item/%d", TEST_CLIENT_ID, TEST_ID))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_ORDER)))
                //assert
                .andExpect(status().isConflict());
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')"),
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (2, 'Kendrick', 'Lamar', 'kendrickL@gmail.com', '0941727477', 'West Newbury')"),
            @Sql(statements = "INSERT INTO item" +
                    " (id, name, price, description, status, seller_id)" +
                    " VALUES (1, 'Laptop', 1222, 'Grey laptop', 'SELLING', 1)"),
            @Sql(statements = "INSERT INTO item_order" +
                    " (id, payment_method, date, client_id, item_id)" +
                    " VALUES (1, 'CASH', '2019-10-25', 2, 1)")
    })
    void whenGetAllByClientShouldRespondOkAndReturnListTest() throws Exception {
        //when-then
        mockMvc.perform(get("/orders/client/" + TEST_CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].item", is(TEST_ITEM.getName())))
                .andExpect(jsonPath("$[0].price", is(TEST_ITEM.getPrice())))
                .andExpect(jsonPath("$[0].clientName",
                        is(TEST_CLIENT.getFirstName() + " " + TEST_CLIENT.getLastName())))
                .andExpect(jsonPath("$[0].sellerName",
                        is(TEST_SELLER.getFirstName() + " " + TEST_SELLER.getLastName())))
                .andExpect(jsonPath("$[0].paymentMethod", is("CASH")));
    }


    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (2, 'Kendrick', 'Lamar', 'kendrickL@gmail.com', '0941727477', 'West Newbury')")
    })
    void whenGetAllByClientEmptyShouldRespondOkTest() throws Exception {
        //when-then
        mockMvc.perform(get("/orders/client/" + TEST_CLIENT_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(jsonPath("$", Matchers.hasSize(0)))
                .andExpect(status().isOk());
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')"),
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (2, 'Kendrick', 'Lamar', 'kendrickL@gmail.com', '0941727477', 'West Newbury')"),
            @Sql(statements = "INSERT INTO item" +
                    " (id, name, price, description, status, seller_id)" +
                    " VALUES (1, 'Laptop', 1222, 'Grey laptop', 'SELLING', 1)"),
            @Sql(statements = "INSERT INTO item_order" +
                    " (id, payment_method, date, client_id, item_id)" +
                    " VALUES (1, 'CASH', '2019-10-25', 2, 1)")
    })
    void whenGetByIdShouldRespondOkTest() throws Exception {
        //when-then
        mockMvc.perform(get("/orders/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item", is(TEST_ITEM.getName())))
                .andExpect(jsonPath("$.price", is(TEST_ITEM.getPrice())))
                .andExpect(jsonPath("$.clientName",
                        is(TEST_CLIENT.getFirstName() + " " + TEST_CLIENT.getLastName())))
                .andExpect(jsonPath("$.sellerName",
                        is(TEST_SELLER.getFirstName() + " " + TEST_SELLER.getLastName())))
                .andExpect(jsonPath("$.paymentMethod", is("CASH")));
    }

    @Test
    void whenGetByNonExistentIdShouldReturnNotFoundTest() throws Exception {
        //when-then
        mockMvc.perform(get("/orders/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(status().isNotFound());
    }
}
