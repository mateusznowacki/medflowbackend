package pl.medflow.medflowbackend.domain.patient_management.dto;

import java.util.List;

public record PatientMedicalUpdateRequest(
    List<String> allergies,
    List<String> medications,
    List<String> chronicDiseases
) {}
