package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.dto.response.ClientsInList;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.exception.AuthorNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional(readOnly = true)
    public Client retrieveClientById(long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new AuthorNotFoundException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ClientsInList retrieveAllClients() {
        return new ClientsInList(clientRepository.findAll());
    }
}
