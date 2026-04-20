package dev.nbcsparta.assignment.schedulemanager.unit.service;

import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonEventResponse;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostEventRequest;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.entity.Event;
import dev.nbcsparta.assignment.schedulemanager.repository.EventRepository;
import dev.nbcsparta.assignment.schedulemanager.service.ClientService;
import dev.nbcsparta.assignment.schedulemanager.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private EventService eventService;

    @Test
    public void createNewEventAndSuccess() {
        long dummyClientId = 1L;
        Client dummyClient = new Client("Test User", "john.doe@dummy.dev", "qwer1234");
        PostEventRequest request = new PostEventRequest("Test Event", "This is a test event.");

        when(clientService.retrieveClientById(dummyClientId))
                .thenReturn(dummyClient);
        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> {
                    Event savedEvent = invocation.getArgument(0);
                    ReflectionTestUtils.setField(savedEvent, "id", 1L);
                    ReflectionTestUtils.setField(savedEvent, "updatedDate", LocalDateTime.now());
                    return savedEvent;
                });

        CommonEventResponse dummyResBody = eventService.createEvent(request, dummyClientId);

        verify(eventRepository).save(any(Event.class));
        Assertions.assertEquals(1L, dummyResBody.id());
        Assertions.assertEquals(request.title(), dummyResBody.title());
        Assertions.assertEquals(request.description(), dummyResBody.description());
        Assertions.assertEquals(dummyClient.getUserName(), dummyResBody.authorName());
        Assertions.assertNotNull(dummyResBody.date());
    }
}
