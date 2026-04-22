package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import dev.nbcsparta.assignment.schedulemanager.dto.request.UpdateClientDetail;
import dev.nbcsparta.assignment.schedulemanager.dto.response.ClientsInList;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonClientDetail;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.exception.AuthorNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.exception.ClientNotAuthorisedException;
import dev.nbcsparta.assignment.schedulemanager.exception.PasswordNotMatchException;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder encoder;

    public ClientService(ClientRepository clientRepository, PasswordEncoder encoder) {
        this.clientRepository = clientRepository;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public Client getClient(long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new AuthorNotFoundException(HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public CommonClientDetail retrieveClientById(long id) {
        return new CommonClientDetail(this.getClient(id));
    }

    @Transactional(readOnly = true)
    public ClientsInList retrieveAllClients() {
        return new ClientsInList(clientRepository.findAll());
    }

    public CommonClientDetail putClientById(
            long clientId,
            @Valid UpdateClientDetail reqBody,
            long sessionId
    ) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new AuthorNotFoundException(HttpStatus.NOT_FOUND));
        if (clientId != sessionId) {
            throw new ClientNotAuthorisedException(HttpStatus.FORBIDDEN, sessionId, clientId);
        }
        if (!client.isPasswordMatch(encoder, reqBody.oldPassword())) {
            throw new PasswordNotMatchException(HttpStatus.BAD_REQUEST);
        }
        client.updateClientDetail(reqBody.userName(), reqBody.email());

        return new CommonClientDetail(client);
    }

    public void deleteClientById(long id, long sessionId) {

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException(HttpStatus.NOT_FOUND));

        if (id != sessionId) {
            throw new ClientNotAuthorisedException(HttpStatus.FORBIDDEN, sessionId, id);
        }
        clientRepository.delete(client);
    }
}
