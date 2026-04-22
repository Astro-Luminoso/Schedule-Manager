package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostLoginRequest;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.exception.AuthorNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.exception.PasswordNotMatchException;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder encoder;

    public LoginService(ClientRepository clientRepository, PasswordEncoder encoder) {
        this.clientRepository = clientRepository;
        this.encoder = encoder;
    }

    public SessionUser executeLogin(PostLoginRequest reqBody) {
        Client user = clientRepository.findByEmail(reqBody.email())
                .orElseThrow(() -> new AuthorNotFoundException(HttpStatus.UNAUTHORIZED));
        if(user.passwordNotMatch(encoder, reqBody.password())) {
            throw new PasswordNotMatchException(HttpStatus.UNAUTHORIZED);
        }

        return SessionUser.from(user);
    }
}
