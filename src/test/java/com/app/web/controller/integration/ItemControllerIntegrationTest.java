package com.app.web.controller.integration;

import com.app.data.dao.ItemDao;
import com.app.data.entity.Client;
import com.app.data.entity.Item;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerIntegrationTest extends ControllerBaseTest {

    @Autowired
    private ItemDao itemDao;

    private static final Long TEST_ID = 1L;

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
            .build();

    private static final Item TEST_UPDATED_ITEM = Item.builder()
            .id(TEST_ID)
            .name("Laptop Updated")
            .price(1555)
            .description("Black laptop updated")
            .build();

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')")
    })
    void whenCreateItemShouldReturnCreatedTest() throws Exception {
        Item expected = TEST_ITEM;

        //when-then
        mockMvc.perform(post("/items/client/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(expected)))
                .andExpect(status().isCreated());//assert

        List<Item> allItems = itemDao.findAll();
        Item actual = allItems.get(0);

        //assert
        assertEquals(1, allItems.size());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(Status.SELLING, actual.getStatus());
        assertEquals(TEST_CLIENT, actual.getSeller());

        assertTrue(Objects.nonNull(actual.getId()) && actual.getId() > 0);
    }

    @Test
    void whenCreateItemByNonExistentClientShouldRespondNotFoundTest() throws Exception {
        //when-then
        mockMvc.perform(post("/items/client/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_ITEM)))
                //assert
                .andExpect(status().isNotFound());
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')"),
            @Sql(statements = "INSERT INTO item" +
                    " (id, name, price, description, status, seller_id)" +
                    " VALUES (1, 'Laptop', 1222, 'Grey laptop', 'SELLING', 1)")
    })
    void whenDeleteItemShouldRespondOkTest() throws Exception {
        //when-then
        mockMvc.perform(delete("/items/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_ITEM)))
                //assert
                .andExpect(status().isOk());
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')"),
            @Sql(statements = "INSERT INTO item" +
                    " (id, name, price, description, status, seller_id)" +
                    " VALUES (1, 'Laptop', 1222, 'Grey laptop', 'SOLD', 1)")
    })
    void whenDeleteSoldItemShouldRespondConflict() throws Exception {
        //when-then
        mockMvc.perform(delete("/items/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_ITEM)))
                //assert
                .andExpect(status().isConflict());
    }

    @Test
    void whenDeleteNonExistentItemShouldRespondNotFoundTest() throws Exception {
        //when-then
        mockMvc.perform(delete("/items/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_ITEM)))
                //assert
                .andExpect(status().isNotFound());
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')"),
            @Sql(statements = "INSERT INTO item" +
                    " (id, name, price, description, status, seller_id)" +
                    " VALUES (1, 'Laptop', 1222, 'Grey laptop', 'SELLING', 1)")
    })
    void whenGetAllForSaleShouldRespondOkAndReturnListTest() throws Exception {
        //when-then
        mockMvc.perform(get("/items/")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[0].price", is(1222)))
                .andExpect(jsonPath("$[0].description", is("Grey laptop")))
                .andExpect(jsonPath("$[0].status", is("SELLING")))
                .andExpect(jsonPath("$[0].seller",
                        is(TEST_CLIENT.getFirstName() + " " + TEST_CLIENT.getLastName())));
    }

    @Test
    void whenGetAllForSaleEmptyShouldRespondOkTest() throws Exception {
        //when-then
        mockMvc.perform(get("/items")
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
            @Sql(statements = "INSERT INTO item" +
                    " (id, name, price, description, status, seller_id)" +
                    " VALUES (1, 'Laptop', 1222, 'Grey laptop', 'SELLING', 1)")
    })
    void whenGetAllSellingByOwnerShouldRespondOkAndReturnListTest() throws Exception {
        //when-then
        mockMvc.perform(get("/items/client/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Laptop")))
                .andExpect(jsonPath("$[0].price", is(1222)))
                .andExpect(jsonPath("$[0].description", is("Grey laptop")))
                .andExpect(jsonPath("$[0].status", is("SELLING")))
                .andExpect(jsonPath("$[0].seller",
                        is(TEST_CLIENT.getFirstName() + " " + TEST_CLIENT.getLastName())));
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')")
    })
    void whenGetAllSellingByOwnerEmptyShouldRespondOkTest() throws Exception {
        //when-then
        mockMvc.perform(get("/items/client/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(jsonPath("$", Matchers.hasSize(0)))
                .andExpect(status().isOk());
    }

    @Test
    void whenGetAllSellingByNonExistentOwnerShouldRespondNorFoundTest() throws Exception {
        //when-then
        mockMvc.perform(get("/items/client/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(status().isNotFound());
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')"),
            @Sql(statements = "INSERT INTO item" +
                    " (id, name, price, description, status, seller_id)" +
                    " VALUES (1, 'Laptop', 1222, 'Grey laptop', 'SELLING', 1)")
    })
    void whenGetByIdShouldRespondOkTest() throws Exception {
        //when-then
        mockMvc.perform(get("/items/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.price", is(1222)))
                .andExpect(jsonPath("$.description", is("Grey laptop")))
                .andExpect(jsonPath("$.status", is("SELLING")))
                .andExpect(jsonPath("$.seller",
                        is(TEST_CLIENT.getFirstName() + " " + TEST_CLIENT.getLastName())));
    }

    @Test
    void whenGetByNonExistentIdShouldReturnNotFoundTest() throws Exception {
        //when-then
        mockMvc.perform(get("/items/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(status().isNotFound());
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')"),
            @Sql(statements = "INSERT INTO item" +
                    " (id, name, price, description, status, seller_id)" +
                    " VALUES (1, 'Laptop', 1222, 'Grey laptop', 'SELLING', 1)")
    })
    void whenUpdateItemShouldRespondOkTest() throws Exception {
        Item expected = TEST_UPDATED_ITEM;

        //when-then
        mockMvc.perform(put("/items/client/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(expected)))
                .andExpect(status().isOk());//assert

        List<Item> allItems = itemDao.findAll();
        Item actual = allItems.get(0);

        //assert
        assertEquals(1, allItems.size());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getPrice(), actual.getPrice());
        assertEquals(Status.SELLING, actual.getStatus());
        assertEquals(TEST_CLIENT, actual.getSeller());

        assertTrue(Objects.nonNull(actual.getId()) && actual.getId() > 0);
    }

    @Test
    void whenUpdateItemByNonExistentClientShouldRespondNotFoundTest() throws Exception {
        //when-then
        mockMvc.perform(put("/items/client/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_UPDATED_ITEM)))
                //assert
                .andExpect(status().isNotFound());
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')"),
            @Sql(statements = "INSERT INTO item" +
                    " (id, name, price, description, status, seller_id)" +
                    " VALUES (1, 'Laptop', 1222, 'Grey laptop', 'SOLD', 1)")
    })
    void whenUpdateSoldItemShouldRespondConflictTest() throws Exception {
        //when-then
        mockMvc.perform(put("/items/client/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_UPDATED_ITEM)))
                //assert
                .andExpect(status().isConflict());
    }
}
