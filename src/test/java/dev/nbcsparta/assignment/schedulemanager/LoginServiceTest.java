package dev.nbcsparta.assignment.schedulemanager;

import dev.nbcsparta.assignment.schedulemanager.config.PasswordEncoder;
import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostLoginRequest;
import dev.nbcsparta.assignment.schedulemanager.entity.Author;
import dev.nbcsparta.assignment.schedulemanager.exception.AuthorNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.exception.PasswordNotMatchException;
import dev.nbcsparta.assignment.schedulemanager.repository.AuthorRepository;
import dev.nbcsparta.assignment.schedulemanager.service.LoginService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private LoginService loginService;

    @Test
    public void testLoginAndSuccess() {
        PostLoginRequest requestDummyUser = new PostLoginRequest("john.doe@dummy.com", "qwer1234");
        Author dummyAuthor = new Author("John Doe", requestDummyUser.email(), requestDummyUser.password());
        when(authorRepository.findByEmail(requestDummyUser.email())).thenReturn(Optional.of(dummyAuthor));
        when(encoder.matches(requestDummyUser.password(), "qwer1234")).thenReturn(true);

        SessionUser testUser = loginService.executeLogin(requestDummyUser);
        Assertions.assertNotNull(testUser);
        Assertions.assertEquals(SessionUser.class, testUser.getClass());
    }

    @Test
    public void testLoginAndPasswordNotMatch() {
        PostLoginRequest requestDummyUser = new PostLoginRequest("jane.doe@dummy.com", "qwer1234");
        String storedPassword = "q1w2e3r4";
        when(authorRepository.findByEmail(requestDummyUser.email()))
                .thenReturn(Optional.of(new Author("Jane Doe", requestDummyUser.email(), storedPassword)));
        when(encoder.matches(requestDummyUser.password(), storedPassword)).thenReturn(false);

        PasswordNotMatchException ex = Assertions.assertThrows(
                PasswordNotMatchException.class,
                () -> loginService.executeLogin(requestDummyUser));

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }

    @Test
    public void testLoginAndAuthorNotFound() {
        PostLoginRequest requestDummyUser = new PostLoginRequest("jane.dommy.com", "qwer1234");
        when(authorRepository.findByEmail(requestDummyUser.email()))
                .thenReturn(Optional.empty());

        AuthorNotFoundException ex = Assertions.assertThrows(
                AuthorNotFoundException.class,
                () -> loginService.executeLogin(requestDummyUser));

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
    }
}
