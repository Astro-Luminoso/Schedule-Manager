package dev.nbcsparta.assignment.schedulemanager;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PostRegisterRequest;
import dev.nbcsparta.assignment.schedulemanager.entity.Author;
import dev.nbcsparta.assignment.schedulemanager.exception.DuplicateUserException;
import dev.nbcsparta.assignment.schedulemanager.repository.AuthorRepository;
import dev.nbcsparta.assignment.schedulemanager.service.RegisterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private RegisterService registerService;

    @Test
    public void testRegisterAndSuccess() {
        PostRegisterRequest reqBody = new PostRegisterRequest("testUser", "test.tester@dummy.dev", "qwer1234");
        Author dummyAuthor = reqBody.toUser();
        when(authorRepository.existsByEmail(reqBody.email())).thenReturn(false);
        when(authorRepository.save(any(Author.class))).thenReturn(dummyAuthor);

        registerService.executeRegister(reqBody);

        verify(authorRepository).save(any(Author.class));
    }

    @Test
    public void testRegisterAndDuplicatedEmailFound() {

        PostRegisterRequest reqBody = new PostRegisterRequest("testUser", "test.tester@dummy.dev", "qwer1234");
        Author dummyAuthor = reqBody.toUser();
        when(authorRepository.existsByEmail(reqBody.email())).thenReturn(true);

        Assertions.assertThrows(DuplicateUserException.class, () -> registerService.executeRegister(reqBody));

    }
}
