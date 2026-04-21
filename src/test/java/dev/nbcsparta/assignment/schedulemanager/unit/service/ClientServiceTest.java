package dev.nbcsparta.assignment.schedulemanager.unit.service;

import dev.nbcsparta.assignment.schedulemanager.dto.response.ClientsInList;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.exception.AuthorNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import dev.nbcsparta.assignment.schedulemanager.service.ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    public void testRetrieveAllClientsAndSuccess() {
        List<Client> dummyClients = List.of(
                new Client("Test User1", "jane.doe@dummy.dev", "qwer1234"),
                new Client("Test User2", "john.doe@dummy.dev", "asdf1234")
        );

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
}
