package pl.medflow.medflowbackend.domain.patient_management.dto;

import jakarta.validation.constraints.*;
import pl.medflow.medflowbackend.domain.shared.embedded.Address;
import pl.medflow.medflowbackend.domain.shared.enums.Gender;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record PatientCreateRequest(

    // --- z User ---
    @NotBlank
    String firstName,

    @NotBlank
    String lastName,

    @Email
    @NotBlank
    String email,

    @NotBlank
    String password, // plain text, zakodujesz w serwisie

    @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Invalid phone number format")
    String phoneNumber,

    Boolean active,
    Boolean emailVerified,
    Instant lastLoginAt,
    Instant passwordUpdatedAt,

    // --- z Patient ---
    String nationalId,
    Gender gender,
    LocalDate dateOfBirth,
    Address address,

    String emergencyContactName,
    String emergencyContactPhone,

    Boolean consentGiven,
    Instant termsAcceptedAt,
    Instant privacyPolicyAcceptedAt,

    List<String> allergies,
    List<String> medications,
    List<String> chronicDiseases,

    String familyDoctorId
) {}
