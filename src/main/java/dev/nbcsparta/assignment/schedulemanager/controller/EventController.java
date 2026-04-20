package dev.nbcsparta.assignment.schedulemanager.controller;

import dev.nbcsparta.assignment.schedulemanager.service.EventService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController (EventService eventService) {
        this.eventService = eventService;
    }


}
