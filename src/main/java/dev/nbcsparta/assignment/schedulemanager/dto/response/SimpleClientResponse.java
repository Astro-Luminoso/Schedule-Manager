package dev.nbcsparta.assignment.schedulemanager.dto.response;

import dev.nbcsparta.assignment.schedulemanager.entity.Client;

public record SimpleClientResponse(String userName, String email) {

    public static SimpleClientResponse from(Client client) {
        return new SimpleClientResponse(client.getUserName(), client.getEmail());
    }
}
