package dev.nbcsparta.assignment.schedulemanager.unit.service;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import dev.nbcsparta.assignment.schedulemanager.dto.request.UpdateClientDetail;
import dev.nbcsparta.assignment.schedulemanager.dto.response.ClientsInList;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonClientDetail;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.exception.AuthorNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.exception.ClientNotAuthorisedException;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import dev.nbcsparta.assignment.schedulemanager.service.ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Mock
    private PasswordEncoder encoder;

    @Test
    public void testRetrieveAllClientsAndSuccess() {
        List<Client> dummys = List.of(
                new Client("Test User1", "jane.doe@dummy.dev", "qwer1234"),
                new Client("Test User2", "john.doe@dummy.dev", "asdf1234")
        );
        List<Client>dummyClients = dummys.stream().peek(client -> {
            ReflectionTestUtils.setField(client, "id", (long) (dummys.indexOf(client) + 1));
            ReflectionTestUtils.setField(client, "updatedDate", LocalDateTime.now());
        }).toList();

        when(clientRepository.findAll()).thenReturn(dummyClients);

        ClientsInList result = clientService.retrieveAllClients();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.total());
    }

    @Test
    public void testGetClientAndSuccess() {
        long dummyClientId = 1L;
        Client dummyClient = new Client("Test User", "jane.doe@dummy.dev", "qwer1234");

        when(clientRepository.findById(dummyClientId)).thenReturn(Optional.of(dummyClient));

        Client dummyResult = clientService.getClient(dummyClientId);

        Assertions.assertNotNull(dummyResult);
        Assertions.assertEquals(dummyClient.getUserName(), dummyResult.getUserName());
        Assertions.assertEquals(dummyClient.getEmail(), dummyResult.getEmail());
    }

    @Test
    public void testGetClientProvided() {
        long dummyClientId = 1L;

        when(clientRepository.findById(dummyClientId)).thenReturn(Optional.empty());

        AuthorNotFoundException ex = Assertions.assertThrows(AuthorNotFoundException.class, () -> clientService.retrieveClientById(dummyClientId));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    public void testPutClientByIdAndSuccess() {
        long dummyClientId = 1L;
        Client dummyClient = new Client("Old Name", "old@dummy.dev", "qwer1234");
        UpdateClientDetail reqBody = new UpdateClientDetail("New Name", "new@dummy.dev", "qwer1234");

        ReflectionTestUtils.setField(dummyClient, "id", dummyClientId);
        ReflectionTestUtils.setField(dummyClient, "updatedDate", LocalDateTime.now());

        when(clientRepository.findById(dummyClientId)).thenReturn(Optional.of(dummyClient));
        when(encoder.matches(reqBody.oldPassword(), "qwer1234")).thenReturn(true);

        CommonClientDetail result = clientService.putClientById(dummyClientId, reqBody, dummyClientId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(dummyClientId, result.id());
        Assertions.assertEquals(reqBody.userName(), result.userName());
        Assertions.assertEquals(reqBody.email(), result.email());
    }

    @Test
    public void testPutClientByIdAndForbidden() {
        long clientId = 1L;
        long anotherSessionId = 2L;
        UpdateClientDetail reqBody = new UpdateClientDetail("New Name", "new@dummy.dev", "q1w2e3r4");
        Client client = new Client("New Name2", "new2@dummy.dev", "q1w2e3r4");
        ReflectionTestUtils.setField(client, "id", 2L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        ClientNotAuthorisedException ex = Assertions.assertThrows(
                ClientNotAuthorisedException.class,
                () -> clientService.putClientById(clientId, reqBody, anotherSessionId)
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    public void testPutClientByIdAndNotFound() {
        long clientId = 1L;
        UpdateClientDetail reqBody = new UpdateClientDetail("New Name", "new@dummy.dev", "qwer1234");

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        AuthorNotFoundException ex = Assertions.assertThrows(
                AuthorNotFoundException.class,
                () -> clientService.putClientById(clientId, reqBody, clientId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    public void testDeleteClientByIdAndSuccess() {
        long clientId = 1L;
        Client dummyClient = new Client("Test User", "jane.doe@dummy.dev", "qwer1234");
        ReflectionTestUtils.setField(dummyClient, "id", clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(dummyClient));

        clientService.deleteClientById(clientId, clientId);
        Assertions.assertEquals("Deleted_User_1", dummyClient.getUserName());
        Assertions.assertNull(dummyClient.getEmail());
    }

    @Test
    public void testDeleteClientByIdAndForbidden() {
        long clientId = 1L;
        long anotherSessionId = 2L;

        Client client = new Client("New Name2", "new2@dummy.dev", "q1w2e3r4");
        ReflectionTestUtils.setField(client, "id", 2L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        ClientNotAuthorisedException ex = Assertions.assertThrows(
                ClientNotAuthorisedException.class,
                () -> clientService.deleteClientById(clientId, anotherSessionId)
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    public void testDeleteClientByIdAndNotFound() {
        long clientId = 1L;

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        AuthorNotFoundException ex = Assertions.assertThrows(
                AuthorNotFoundException.class,
                () -> clientService.deleteClientById(clientId, clientId)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}
