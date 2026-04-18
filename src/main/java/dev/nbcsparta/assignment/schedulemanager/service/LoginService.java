package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostLoginRequest;
import dev.nbcsparta.assignment.schedulemanager.entity.Author;
import dev.nbcsparta.assignment.schedulemanager.exception.AuthorNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.exception.PasswordNotMatchException;
import dev.nbcsparta.assignment.schedulemanager.repository.AuthorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final AuthorRepository authorRepository;
    private final PasswordEncoder encoder;

    public LoginService(AuthorRepository authorRepository, PasswordEncoder encoder) {
        this.authorRepository = authorRepository;
        this.encoder = encoder;
    }

    public SessionUser executeLogin(PostLoginRequest reqBody) {
        Author author = authorRepository.findByEmail(reqBody.email())
                .orElseThrow(() -> new AuthorNotFoundException(HttpStatus.UNAUTHORIZED));
        if(!author.isPasswordMatch(encoder, reqBody.password())) {
            throw new PasswordNotMatchException(HttpStatus.UNAUTHORIZED);
        }

        return SessionUser.from(author);
    }
}
