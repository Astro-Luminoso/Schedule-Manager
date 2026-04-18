package dev.nbcsparta.assignment.schedulemanager.dto;

import dev.nbcsparta.assignment.schedulemanager.entity.Author;

public record SessionUser(Long id, String email) {

    public static SessionUser from(Author author) {

        return new SessionUser(author.getId(), author.getEmail());
    }
}
