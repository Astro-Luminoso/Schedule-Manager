package dev.nbcsparta.assignment.schedulemanager.controller;

import dev.nbcsparta.assignment.schedulemanager.dto.SessionUser;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PatchEventRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.request.PostEventRequest;
import dev.nbcsparta.assignment.schedulemanager.dto.response.CommonEventResponse;
import dev.nbcsparta.assignment.schedulemanager.dto.response.EventListResponse;
import dev.nbcsparta.assignment.schedulemanager.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
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
        CommonEventResponse resBody = eventService.createEvent(reqBody, sessionUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(resBody);
    }

    // UNIT test is not implemented for the below method because it is too simple to test.
    @GetMapping
    public ResponseEntity<EventListResponse> getEvents(
            @RequestParam(required = false) Long authorId
    ) {
        EventListResponse resBody = eventService.getEvents(authorId);
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonEventResponse> getEventById(@PathVariable Long id) {
        CommonEventResponse resBody = eventService.getEventById(id);
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommonEventResponse> updateEvent(
            @PathVariable Long id,
            @RequestBody PatchEventRequest reqBody,
            @SessionAttribute(name = "LOGIN_USER", required = false) SessionUser sessionUser
    ) {
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        CommonEventResponse resBody = eventService.updateEvent(id, reqBody, sessionUser.id());
        return ResponseEntity.status(HttpStatus.OK).body(resBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @SessionAttribute(name = "LOGIN_USER", required = false) SessionUser sessionUser
    ) {
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        eventService.deleteEvent(id, sessionUser.id());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
