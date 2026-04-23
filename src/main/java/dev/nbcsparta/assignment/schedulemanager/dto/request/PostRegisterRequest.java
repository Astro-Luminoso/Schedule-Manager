package dev.nbcsparta.assignment.schedulemanager.dto.request;

import dev.nbcsparta.assignment.schedulemanager.entity.Client;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRegisterRequest(
        @NotBlank
        @Size(min = 4)
        String userName,

        @Email
        String email,

        @NotBlank
        @Size(min = 8)
        String password
) {
    public Client toUser(String encodedPassword) {
        return new Client(userName, email, encodedPassword);
    }
}
