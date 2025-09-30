package pl.medflow.medflowbackend.domain.staff_management.dto;

import pl.medflow.medflowbackend.domain.shared.embedded.RoomLocation;
import pl.medflow.medflowbackend.domain.staff_management.MedicalStaffPosition;

public record MedicalStaffSummaryResponse(
        String phoneNumber,
        MedicalStaffPosition position,
        String department,
        RoomLocation assignedRoom,
        String licenseNumber
) {
}
