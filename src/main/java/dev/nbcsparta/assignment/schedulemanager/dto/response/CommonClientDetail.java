package dev.nbcsparta.assignment.schedulemanager.dto.response;

import dev.nbcsparta.assignment.schedulemanager.entity.Client;

public record CommonClientDetail(Long id, String userName, String email, String date) {

    public static CommonClientDetail from(Client client) {
        return new CommonClientDetail(client.getId(), client.getUserName(), client.getEmail(), client.getDate());
    }
}
