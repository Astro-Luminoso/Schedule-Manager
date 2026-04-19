package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PostRegisterRequest;
import dev.nbcsparta.assignment.schedulemanager.exception.DuplicateUserException;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final ClientRepository clientRepository;

    public RegisterService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void executeRegister(PostRegisterRequest reqBody) {
        if(clientRepository.existsByEmail(reqBody.email())) {
            throw new DuplicateUserException(HttpStatus.BAD_REQUEST);
        }
        clientRepository.save(reqBody.toUser());
    }
}
