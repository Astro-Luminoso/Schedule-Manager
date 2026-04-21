package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.exception.AuthorNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client retrieveClientById(long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new AuthorNotFoundException(HttpStatus.NOT_FOUND));
    }
}
