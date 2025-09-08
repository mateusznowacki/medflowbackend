package pl.medflow.medflowbackend.domain.staff_management;

import pl.medflow.medflowbackend.domain.shared.embedded.RoomLocation;
import pl.medflow.medflowbackend.domain.shared.enums.MedicalStaffPosition;

public record StaffResponse(
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
