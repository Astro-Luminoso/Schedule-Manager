package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PostRegisterRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.SimpleClientResponse;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final ClientService clientService;

    public RegisterService(ClientService clientService) {
        this.clientService = clientService;
    }

    public SimpleClientResponse executeRegister(PostRegisterRequest reqBody) {
        Client client = clientService.saveNewClient(reqBody);
        return SimpleClientResponse.from(client);
    }
}
