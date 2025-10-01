package pl.medflow.medflowbackend.domain.staff_management.dto;

import jakarta.validation.constraints.Email;

public record UpdateContactRequest(
        @Email String newEmail,
        String phone
) {}
