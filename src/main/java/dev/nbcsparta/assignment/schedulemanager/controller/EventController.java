package dev.nbcsparta.assignment.schedulemanager.controller;

import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostEventRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonEventResponse;
import dev.nbcsparta.assignment.schedulemanager.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController (EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<CommonEventResponse> createNewEvent(
            @Valid @RequestBody PostEventRequest reqBody,
            @SessionAttribute(name = "LOGIN_USER", required = false) SessionUser sessionUser
            ) {
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CommonEventResponse response = eventService.createEvent(reqBody, sessionUser.id());
        return ResponseEntity.status(201).body(response);
    }


}
