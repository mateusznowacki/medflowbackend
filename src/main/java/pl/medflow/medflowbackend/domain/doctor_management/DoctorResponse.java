package pl.medflow.medflowbackend.domain.doctor_management;

import pl.medflow.medflowbackend.domain.shared.embedded.RoomLocation;

import java.util.List;

public record DoctorResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String licenseNumber,
        String specialization,
        String department,
        RoomLocation office,
        boolean acceptingPatients,
        List<String> procedureIds
) {}
