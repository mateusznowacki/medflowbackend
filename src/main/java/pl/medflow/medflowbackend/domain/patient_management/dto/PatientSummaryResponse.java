package pl.medflow.medflowbackend.domain.patient_management.dto;

public record PatientSummaryResponse(
    String id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber
) {}
