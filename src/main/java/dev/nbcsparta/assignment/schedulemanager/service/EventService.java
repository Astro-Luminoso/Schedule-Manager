package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PostEventRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonEventResponse;
import dev.nbcsparta.assignment.schedulemanager.dto.response.EventListResponse;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.entity.Event;
import dev.nbcsparta.assignment.schedulemanager.exception.ClientNotAuthorizationException;
import dev.nbcsparta.assignment.schedulemanager.exception.EventNotFoundException;
import dev.nbcsparta.assignment.schedulemanager.repository.EventRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final ClientService clientService;

    public EventService(EventRepository eventRepository, ClientService clientService) {
        this.eventRepository = eventRepository;
        this.clientService = clientService;
    }

    public CommonEventResponse createEvent(PostEventRequest reqBody, long clientId) {
        Client author = clientService.retrieveClientById(clientId);
        Event event =  new Event(reqBody.title(), reqBody.description(), author);
        Event newEvent = eventRepository.save(event);

        return CommonEventResponse.from(newEvent);
    }

    @Transactional(readOnly = true)
    public EventListResponse getEvents(Long authorId) {
        List<Event> events = (authorId == null) ? eventRepository.findAll() : eventRepository.findByAuthorId(authorId);

        return EventListResponse.from(events.stream().map(CommonEventResponse::from).toList());
    }

    public CommonEventResponse updateEvent(
            Long id,
            @Valid PostEventRequest reqBody,
            Long sessionUserId
    ) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(HttpStatus.NOT_FOUND, id));

        if (!event.getAuthor().getId().equals(sessionUserId)) {
            throw new ClientNotAuthorizationException(HttpStatus.FORBIDDEN, sessionUserId, event.getAuthor().getId());
        }

        event.updateEvent(reqBody.title(), reqBody.description());
        return CommonEventResponse.from(event);
    }
}
