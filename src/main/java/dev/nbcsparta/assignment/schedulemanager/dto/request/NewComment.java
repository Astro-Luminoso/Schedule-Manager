package dev.nbcsparta.assignment.schedulemanager.dto.request;

import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import dev.nbcsparta.assignment.schedulemanager.entity.Comment;
import dev.nbcsparta.assignment.schedulemanager.entity.Event;

public record NewComment(String content, Long eventId) {

    public Comment toEntity(Event event, Client client) {
        return new Comment(content, event, client);
    }
}
