package dev.nbcsparta.assignment.schedulemanager.service;

import dev.nbcsparta.assignment.schedulemanager.dto.request.PostEventRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonEventResponse;
import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.entity.Event;
import dev.nbcsparta.assignment.schedulemanager.repository.EventRepository;
import org.springframework.stereotype.Service;

@Service
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
}
