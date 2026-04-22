package dev.nbcsparta.assignment.schedulemanager.unit.service;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PostRegisterRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.SimpleClientResponse;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.exception.DuplicateUserException;
import dev.nbcsparta.assignment.schedulemanager.service.ClientService;
import dev.nbcsparta.assignment.schedulemanager.service.RegisterService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterServiceTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private RegisterService registerService;


    @Test
    public void testRegisterAndSuccess() {
        PostRegisterRequest reqBody = new PostRegisterRequest("testUser", "test.tester@dummy.dev", "qwer1234");
        Client dummyAuthor = reqBody.toUser();
        when(clientService.saveNewClient(reqBody)).thenReturn(dummyAuthor);

        SimpleClientResponse dummyResBody = registerService.executeRegister(reqBody);

        Assertions.assertNotNull(dummyResBody);
        Assertions.assertEquals(dummyResBody.userName(), dummyAuthor.getUserName());
        Assertions.assertEquals(dummyResBody.email(), dummyAuthor.getEmail());
    }

    @Test
    public void testRegisterAndDuplicatedEmailFound() {

        PostRegisterRequest reqBody = new PostRegisterRequest("testUser", "test.tester@dummy.dev", "qwer1234");
        when(clientService.saveNewClient(reqBody)).thenThrow(new DuplicateUserException(HttpStatus.BAD_REQUEST));

        DuplicateUserException ex = Assertions.assertThrows(DuplicateUserException.class,
                () -> registerService.executeRegister(reqBody));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
}
