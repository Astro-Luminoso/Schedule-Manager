package dev.nbcsparta.assignment.schedulemanager.unit.service;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PatchEventRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonEventResponse;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostEventRequest;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.entity.Event;
import dev.nbcsparta.assignment.schedulemanager.exception.ClientNotAuthorisedException;
import dev.nbcsparta.assignment.schedulemanager.exception.EventNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.repository.EventRepository;
import dev.nbcsparta.assignment.schedulemanager.service.ClientService;
import dev.nbcsparta.assignment.schedulemanager.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

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
    public void testCreateNewEventAndSuccess() {
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

    @Test
    public void testPatchEventAndSuccess() {
        long dummyClientId = 1L;
        Client dummyClient = new Client("Test User", "john.doe@dummy.dev", "qwer1234");
        PatchEventRequest dummyReqBody = new PatchEventRequest("Test Event", "This is a test event.");
        Event dummyEvent = new Event("dummyReqBody.title()", "dummyReqBody.description()", dummyClient);
        ReflectionTestUtils.setField(dummyClient, "id", dummyClientId);
        ReflectionTestUtils.setField(dummyEvent, "id", 1L);
        ReflectionTestUtils.setField(dummyEvent, "updatedDate", LocalDateTime.now());

        when(eventRepository.findById(1L)).thenReturn(Optional.of(dummyEvent));

        CommonEventResponse dummyResBody = eventService.updateEvent(dummyEvent.getId(), dummyReqBody, dummyClientId);

        Assertions.assertEquals(dummyEvent.getId(), dummyResBody.id());
        Assertions.assertEquals(dummyReqBody.title(), dummyResBody.title());
        Assertions.assertEquals(dummyReqBody.description(), dummyResBody.description());
        Assertions.assertEquals(dummyClient.getUserName(), dummyResBody.authorName());
    }

    @Test
    public void testPatchEventAndFailByNotFound() {
        long dummyClientId = 1L;
        PatchEventRequest dummyReqBody = new PatchEventRequest("Test Event", "This is a test event.");

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        EventNotFoundException ex = Assertions.assertThrows(EventNotFoundException.class, () ->
            eventService.updateEvent(1L, dummyReqBody, dummyClientId)
        );
        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    public void testPatchEventAndFailByForbidden() {
        long dummyClientId = 1L;
        Client dummyClient = new Client("Test User", "dummy@guruguru.com", "qwer1234");
        ReflectionTestUtils.setField(dummyClient, "id", 2L);
        PatchEventRequest dummyReqBody = new PatchEventRequest("Test Event", "This is a test event.");
        Event dummyEvent = new Event("dummyReqBody.title()", "dummyReqBody.description()", dummyClient);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(dummyEvent));

        ClientNotAuthorisedException ex = Assertions.assertThrows(ClientNotAuthorisedException.class, () ->
            eventService.updateEvent(1L, dummyReqBody, dummyClientId)
        );
        Assertions.assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    public void testDeleteEventAndSuccess() {
        Client dummyClient = new Client("Test User", "john.doe@dummy.dev", "qwer1234");
        Event dummyEvent = new Event("dummyReqBody.title()", "dummyReqBody.description()", dummyClient);
        ReflectionTestUtils.setField(dummyClient, "id", 1L);
        ReflectionTestUtils.setField(dummyEvent, "id", 1L);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(dummyEvent));

        eventService.deleteEvent(dummyEvent.getId(), dummyClient.getId());
        verify(eventRepository).delete(dummyEvent);
    }

    @Test
    public void testDeleteEventFailedByNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        EventNotFoundException ex = Assertions.assertThrows(EventNotFoundException.class, () ->
            eventService.deleteEvent(1L, 1L)
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    public void testDeleteEventFailedByForBidden() {
        Client dummyClient = new Client("Test User", "dummy@guruguru.com", "qwer1234");
        ReflectionTestUtils.setField(dummyClient, "id", 2L);
        Event dummyEvent = new Event("dummyReqBody.title()", "dummyReqBody.description()", dummyClient);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(dummyEvent));

        ClientNotAuthorisedException ex = Assertions.assertThrows(ClientNotAuthorisedException.class, () ->
            eventService.deleteEvent(1L, 1L)
        );
        Assertions.assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());


    }
}
