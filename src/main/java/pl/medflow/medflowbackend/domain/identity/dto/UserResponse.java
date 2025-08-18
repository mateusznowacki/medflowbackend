package pl.medflow.medflowbackend.domain.user.dto;

import lombok.Data;
import pl.medflow.medflowbackend.domain.shared.enums.Gender;
import pl.medflow.medflowbackend.domain.shared.enums.MedicalStaffPosition;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.shared.embedded.Address;
import pl.medflow.medflowbackend.domain.shared.embedded.RoomLocation;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserResponse {
    
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Role role;
    private boolean active;
    private boolean emailVerified;
    private Instant lastLoginAt;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Doctor specific fields
    private String licenseNumber;
    private String specialization;
    private String department;
    private RoomLocation office;
    private List<String> procedureIds;
    private boolean acceptingPatients;
    
    // Patient specific fields
    private String nationalId;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Address address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private boolean consentGiven;
    private Instant termsAcceptedAt;
    private Instant privacyPolicyAcceptedAt;
    private List<String> allergies;
    private List<String> medications;
    private List<String> chronicDiseases;
    private String familyDoctorId;
    
    // MedicalStaff specific fields
    private MedicalStaffPosition position;
    private RoomLocation assignedRoom;
} 