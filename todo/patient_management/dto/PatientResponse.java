package pl.medflow.medflowbackend.todo.patient_management.dto;

import pl.medflow.medflowbackend.domain.shared.embedded.Address;
import pl.medflow.medflowbackend.domain.shared.enums.Gender;

import java.time.LocalDate;
import java.util.List;

public record PatientResponse(
    String id,
    String firstName,
    String lastName,
    String email,
    String phoneNumber,
    String nationalId,
    Gender gender,
    LocalDate dateOfBirth,
    Address address,
    String emergencyContactName,
    String emergencyContactPhone,
    boolean consentGiven,
    List<String> allergies,
    List<String> medications,
    List<String> chronicDiseases,
    String familyDoctorId
) {}
