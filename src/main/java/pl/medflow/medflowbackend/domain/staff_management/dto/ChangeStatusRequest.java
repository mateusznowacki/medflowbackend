package pl.medflow.medflowbackend.domain.staff_management.dto;

public record ChangeStatusRequest(
        boolean active,
        String reason
) {}
