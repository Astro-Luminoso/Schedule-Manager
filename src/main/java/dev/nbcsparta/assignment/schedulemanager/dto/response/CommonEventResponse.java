package dev.nbcsparta.assignment.schedulemanager.dto.response;

import dev.nbcsparta.assignment.schedulemanager.entity.Event;

public record CommonEventResponse(long id, String title, String description,String date, String authorName) {

    public static CommonEventResponse from(Event event) {

        return new CommonEventResponse(event.getId(), event.getTitle(), event.getDescription(),event.getDate(), event.getAuthorName());
    }
}
