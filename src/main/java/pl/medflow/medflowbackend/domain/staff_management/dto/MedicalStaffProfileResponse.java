package pl.medflow.medflowbackend.domain.staff_management.dto;

import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.staff_management.MedicalStaffPosition;

public record MedicalStaffProfileResponse(
        String accountId,
        String email,
        String firstName,
        String lastName,
        Role role,
        String phoneNumber,
        boolean active,
        MedicalStaffPosition position,
        String department,
        String assignedRoom, // simplified string representation
        String licenseNumber
) {}
