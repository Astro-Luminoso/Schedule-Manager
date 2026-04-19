package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PostRegisterRequest;
import dev.nbcsparta.assignment.schedulemanager.exception.DuplicateUserException;
import dev.nbcsparta.assignment.schedulemanager.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final AuthorRepository authorRepository;

    public RegisterService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public void executeRegister(PostRegisterRequest reqBody) {
        if(authorRepository.existsByEmail(reqBody.email())) {
            throw new DuplicateUserException(HttpStatus.BAD_REQUEST);
        }
        authorRepository.save(reqBody.toUser());
    }
}
