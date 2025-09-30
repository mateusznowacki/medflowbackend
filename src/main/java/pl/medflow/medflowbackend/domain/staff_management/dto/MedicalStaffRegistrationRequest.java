package pl.medflow.medflowbackend.domain.staff_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import pl.medflow.medflowbackend.domain.shared.embedded.RoomLocation;
import pl.medflow.medflowbackend.domain.staff_management.MedicalStaffPosition;

public record MedicalStaffRegistrationRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email @NotBlank String email,
        @NotBlank String password,
        @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Invalid phone number format") String phoneNumber,
        MedicalStaffPosition position,
        String department,
        RoomLocation assignedRoom,
        String licenseNumber
) {
}