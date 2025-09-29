package pl.medflow.medflowbackend.todo.patient_management.dto;

public record PatientSummaryResponse(
    String id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber
) {}
