package dev.nbcsparta.assignment.schedulemanager.dto.response;

public record SimpleClientResponse(String userName, String email) {

    public static SimpleClientResponse from(String userName, String email) {
        return new SimpleClientResponse(userName, email);
    }
}
