package dev.nbcsparta.assignment.schedulemanager.dto;

import dev.nbcsparta.assignment.schedulemanager.entity.Client;

public record SessionUser(Long id, String email) {

    public static SessionUser from(Client client) {

        return new SessionUser(client.getId(), client.getEmail());
    }
}
