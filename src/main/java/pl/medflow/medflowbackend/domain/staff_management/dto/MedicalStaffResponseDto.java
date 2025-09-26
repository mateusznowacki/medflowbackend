package pl.medflow.medflowbackend.domain.staff_management.dto;

import pl.medflow.medflowbackend.domain.shared.embedded.RoomLocation;
import pl.medflow.medflowbackend.domain.staff_management.model.MedicalStaffPosition;

public record MedicalStaffResponseDto(
        String id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        MedicalStaffPosition position,
        String department,
        RoomLocation assignedRoom,
        String licenseNumber
) {}
