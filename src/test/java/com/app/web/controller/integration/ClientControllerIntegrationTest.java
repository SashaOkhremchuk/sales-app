package com.app.web.controller.integration;

import com.app.data.dao.ClientDao;
import com.app.data.entity.Client;
import com.app.web.controller.ControllerBaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.web.util.NestedServletException;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClientControllerIntegrationTest extends ControllerBaseTest {

    @Autowired
    private ClientDao clientDao;

    private static final Long TEST_ID = 1L;

    private static final Client TEST_CLIENT = Client.builder()
            .firstName("John")
            .lastName("Cena")
            .email("cenajohn@gmail.com")
            .phoneNumber("0981727477")
            .address("West Newbury")
            .build();

    private static final Client TEST_UPDATED_CLIENT = Client.builder()
            .id(1L)
            .firstName("John")
            .lastName("Smith")
            .email("cenajohn@gmail.com")
            .phoneNumber("0981727477")
            .address("Kiev")
            .build();

    private static final Client INVALID_CLIENT = Client.builder()
            .firstName("John")
            .lastName("Cena")
            .email("cenajo")
            .phoneNumber("45453")
            .address("West Newbury")
            .build();

    @Test
    void whenCreateClientShouldReturnCreatedTest() throws Exception {
        Client expected = TEST_CLIENT;

        //when-then
        mockMvc.perform(post("/clients")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(expected)))
                .andExpect(status().isCreated());//assert

        List<Client> allManufacturers = clientDao.findAll();
        Client actual = allManufacturers.get(0);

        //assert
        assertEquals(1, allManufacturers.size());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
        assertEquals(expected.getAddress(), actual.getAddress());

        assertTrue(Objects.nonNull(actual.getId()) && actual.getId() > 0);
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')")
    })
    void whenCreateExistingClientShouldReturnConflictTest() {
        //when-then
        Executable ex = () -> mockMvc.perform(post("/clients")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_CLIENT)))
                //assert
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Such client is already exists")));

        assertThrows(NestedServletException.class, ex);
    }

    @Test
    void whenCreateInvalidClientShouldRespondBadRequestTest() throws Exception {
        //when-then
        mockMvc.perform(post("/clients")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(INVALID_CLIENT)))
                //assert
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetAllEmptyShouldRespondOkTest() throws Exception {
        //when-then
        mockMvc.perform(get("/clients")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk());
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')")
    })
    void whenGetAllShouldRespondOkAndReturnListTest() throws Exception {
        //when-then
        mockMvc.perform(get("/clients")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[0].lastName", is("Cena")))
                .andExpect(jsonPath("$[0].email", is("cenajohn@gmail.com")))
                .andExpect(jsonPath("$[0].phoneNumber", is("0998877654")))
                .andExpect(jsonPath("$[0].address", is("West Newbury")));
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')")
    })
    void whenGetByIdShouldRespondOkTest() throws Exception {
        //when-then
        mockMvc.perform(get("/clients/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Cena")))
                .andExpect(jsonPath("$.email", is("cenajohn@gmail.com")))
                .andExpect(jsonPath("$.phoneNumber", is("0998877654")))
                .andExpect(jsonPath("$.address", is("West Newbury")));
    }

    @Test
    void whenFindByNonExistentIdShouldReturnNotFoundTest() throws Exception {
        //when-then
        mockMvc.perform(get("/clients/" + TEST_ID)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //assert
                .andExpect(status().isNotFound());
    }

    @Test
    @SqlGroup({
            @Sql(statements = "INSERT INTO client" +
                    " (id, first_name, last_name, email, phone_number, address)" +
                    " VALUES (1, 'John', 'Cena', 'cenajohn@gmail.com', '0998877654', 'West Newbury')")
    })
    void whenUpdateClientShouldReturnOkTest() throws Exception {
        //when-then
        mockMvc.perform(put("/clients")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_UPDATED_CLIENT)))
                .andExpect(status().isOk());//assert

        List<Client> allManufacturers = clientDao.findAll();
        Client actual = allManufacturers.get(0);

        //assert
        assertEquals(1, allManufacturers.size());
        assertEquals(TEST_UPDATED_CLIENT.getFirstName(), actual.getFirstName());
        assertEquals(TEST_UPDATED_CLIENT.getLastName(), actual.getLastName());
        assertEquals(TEST_UPDATED_CLIENT.getEmail(), actual.getEmail());
        assertEquals(TEST_UPDATED_CLIENT.getPhoneNumber(), actual.getPhoneNumber());
        assertEquals(TEST_UPDATED_CLIENT.getAddress(), actual.getAddress());

        assertTrue(Objects.nonNull(actual.getId()) && actual.getId() > 0);
    }

    @Test
    void whenUpdateClientWithNonExistentIdShouldReturnConflictTest() {
        //when-then
        Executable ex = () -> mockMvc.perform(put("/clients")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(TEST_UPDATED_CLIENT)))
                //assert
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", is("Error when update client")));

        assertThrows(AssertionError.class, ex);
    }

    @Test
    void whenUpdateInvalidClientShouldRespondBadRequestTest() throws Exception {
        //when-then
        mockMvc.perform(post("/clients")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(OBJECT_MAPPER.writeValueAsString(INVALID_CLIENT)))
                //assert
                .andExpect(status().isBadRequest());
    }
}
