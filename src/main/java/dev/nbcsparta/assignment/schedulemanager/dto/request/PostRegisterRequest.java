package dev.nbcsparta.assignment.schedulemanager.dto.request;

import dev.nbcsparta.assignment.schedulemanager.entity.Author;
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
    public Author toUser() {
        return new Author(userName, email, password);
    }
}
