package dev.nbcsparta.assignment.schedulemanager;

import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostLoginRequest;
import dev.nbcsparta.assignment.schedulemanager.entity.Author;
import dev.nbcsparta.assignment.schedulemanager.exception.AuthorNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.exception.PasswordNotMatchException;
import dev.nbcsparta.assignment.schedulemanager.repository.AuthorRepository;
import dev.nbcsparta.assignment.schedulemanager.service.LoginService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.when;

public class LoginServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private LoginService loginService;

    @Test
    public void testLoginAndSuccess() {
        PostLoginRequest requestDummyUser = new PostLoginRequest("john.doe@dummy.com", "qwer1234");
        when(authorRepository.findByEmail(requestDummyUser.email()))
                .thenReturn(new Author(requestDummyUser.email(), requestDummyUser.password()));

        SessionUser testUser = loginService.executeLogin(requestDummyUser);
        Assertions.assertNotNull(testUser);
        Assertions.assertEquals(SessionUser.class, testUser.getClass());
    }

    @Test
    public void testLoginAndPasswordNotMatch() {
        PostLoginRequest requestDummyUser = new PostLoginRequest("jane.doe@dummy.com", "qwer1234");
        when(authorRepository.findByEmail(requestDummyUser.email()))
                .thenReturn(new Author(requestDummyUser.email(), "q1w2e3r4"));

        PasswordNotMatchException ex = Assertions.assertThrows(
                PasswordNotMatchException.class,
                () -> loginService.executeLogin(requestDummyUser));

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    public void testLoginAndAuthorNotFound() {
        PostLoginRequest requestDummyUser = new PostLoginRequest("jane.doe@dummy.com", "qwer1234");
        when(authorRepository.findByEmail(requestDummyUser.email()))
                .thenReturn(null);

        AuthorNotFoundException ex = Assertions.assertThrows(
                AuthorNotFoundException.class,
                () -> loginService.executeLogin(requestDummyUser));

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());

    }
}
