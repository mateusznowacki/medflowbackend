package pl.medflow.medflowbackend.domain.staff_management.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateNameRequest(
        @NotBlank String firstName,
        @NotBlank String lastName
) {}
