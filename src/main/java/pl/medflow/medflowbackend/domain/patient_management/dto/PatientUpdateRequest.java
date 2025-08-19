package pl.medflow.medflowbackend.domain.patient_management.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import pl.medflow.medflowbackend.domain.shared.embedded.Address;
import pl.medflow.medflowbackend.domain.shared.enums.Gender;

import java.time.LocalDate;
import java.util.List;

@Builder
public record PatientUpdateRequest(

    String firstName,
    String lastName,

    @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Invalid phone number format")
    String phoneNumber,

    String nationalId,
    Gender gender,
    LocalDate dateOfBirth,
    Address address,

    String emergencyContactName,
    String emergencyContactPhone,

    List<String> allergies,
    List<String> medications,
    List<String> chronicDiseases,
    String familyDoctorId
) {}
