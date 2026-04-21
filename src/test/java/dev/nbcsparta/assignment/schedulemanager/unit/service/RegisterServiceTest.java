package dev.nbcsparta.assignment.schedulemanager.unit.service;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PostRegisterRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.SimpleClientResponse;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.exception.DuplicateUserException;
import dev.nbcsparta.assignment.schedulemanager.repository.ClientRepository;
import dev.nbcsparta.assignment.schedulemanager.service.RegisterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private RegisterService registerService;    /* mock jpa meta model - jpa auditing is enabled */

    @Test
    public void testRegisterAndSuccess() {
        PostRegisterRequest reqBody = new PostRegisterRequest("testUser", "test.tester@dummy.dev", "qwer1234");
        Client dummyAuthor = reqBody.toUser();
        when(clientRepository.existsByEmail(reqBody.email())).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(dummyAuthor);

        SimpleClientResponse dummyResBody = registerService.executeRegister(reqBody);

        verify(clientRepository).save(any(Client.class));
        Assertions.assertNotNull(dummyResBody);
        Assertions.assertEquals(dummyResBody.userName(), dummyAuthor.getUserName());
        Assertions.assertEquals(dummyResBody.email(), dummyAuthor.getEmail());
    }

    @Test
    public void testRegisterAndDuplicatedEmailFound() {

        PostRegisterRequest reqBody = new PostRegisterRequest("testUser", "test.tester@dummy.dev", "qwer1234");
        when(clientRepository.existsByEmail(reqBody.email())).thenReturn(true);

        DuplicateUserException ex = Assertions.assertThrows(DuplicateUserException.class,
                () -> registerService.executeRegister(reqBody));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
}
