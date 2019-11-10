package com.app.data.dao.impl;

import com.app.Application;
import com.app.data.dao.ClientDao;
import com.app.data.dao.repository.ClientRepository;
import com.app.data.entity.Client;
import com.app.exception.DBException;
import com.app.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
public class ClientDaoMockTest {

    private static final long TEST_ID = 1L;

    private static final Client TEST_CLIENT = Client.builder()
            .firstName("John")
            .lastName("Cena")
            .email("cenajohn@gmail.com")
            .phoneNumber("0981727477")
            .address("West Newbury")
            .build();

    private static final Client TEST_CLIENT_EXPECTED = Client.builder()
            .id(TEST_ID)
            .firstName("John")
            .lastName("Cena")
            .email("cenajohn@gmail.com")
            .phoneNumber("0981727477")
            .address("West Newbury")
            .build();

    @Autowired
    private ClientDao clientDao;

    @MockBean
    private ClientRepository mockClientRepository;

    @Test
    void whenCreateClientShouldReturnClientWithIdTest() {
        //given
        when(mockClientRepository.save(TEST_CLIENT)).thenReturn(TEST_CLIENT_EXPECTED);
        //when
        Client actual = clientDao.save(TEST_CLIENT);
        //then
        assertEquals(TEST_CLIENT_EXPECTED, actual);
    }

    @Test
    void whenCreateExistingClientShouldThrowDBExceptionTest() {
        //given
        when(mockClientRepository.save(TEST_CLIENT)).thenThrow(DataIntegrityViolationException.class);
        //when-then
        assertThrows(DBException.class, () -> clientDao.save(TEST_CLIENT));
    }

    @Test
    void whenFindAllShouldReturnListOfClientsTest() {
        //given
        when(mockClientRepository.findAll()).thenReturn(List.of(TEST_CLIENT_EXPECTED));
        //when
        List<Client> actual = clientDao.findAll();
        //then
        assertEquals(List.of(TEST_CLIENT_EXPECTED), actual);
    }

    @Test
    void whenFindByIdShouldReturnClientTest() {
        //given
        when(mockClientRepository.findById(TEST_ID)).thenReturn(Optional.ofNullable(TEST_CLIENT_EXPECTED));
        //when
        Client actual = clientDao.findById(TEST_ID);
        //then
        assertEquals(TEST_CLIENT_EXPECTED, actual);
    }

    @Test
    void whenFindByIdReturnNullShouldThrowNotFoundExceptionTest() {
        //given
        when(mockClientRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        //when-then
        assertThrows(NotFoundException.class, () -> clientDao.findById(TEST_ID));
    }

    @Test
    void deleteTest() {
        //when
        clientDao.delete(TEST_ID);
        //then
        verify(mockClientRepository).deleteById(TEST_ID);
    }

    @Test
    void whenUpdateShouldReturnClientTest(){
        //given
        when(mockClientRepository.save(TEST_CLIENT)).thenReturn(TEST_CLIENT_EXPECTED);
        //when
        Client actual = clientDao.update(TEST_CLIENT);
        //then
        assertEquals(TEST_CLIENT_EXPECTED, actual);
    }

    @Test
    void whenUpdateNonExistentClientShouldThrowDBException(){
        //given
        when(mockClientRepository.save(TEST_CLIENT)).thenThrow(DataIntegrityViolationException.class);
        //when-then
        assertThrows(DBException.class, () -> clientDao.update(TEST_CLIENT));
    }
}
