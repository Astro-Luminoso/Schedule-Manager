package dev.nbcsparta.assignment.schedulemanager.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateClientDetail(

        @NotBlank
        @Size(min = 4)
        String userName,

        @Email
        String email,

        @NotBlank
        @Size(min = 8)
        String oldPassword
) {
}
