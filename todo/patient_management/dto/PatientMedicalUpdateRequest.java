package pl.medflow.medflowbackend.todo.patient_management.dto;

import java.util.List;

public record PatientMedicalUpdateRequest(
    List<String> allergies,
    List<String> medications,
    List<String> chronicDiseases
) {}
