package pl.medflow.medflowbackend.domain.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import pl.medflow.medflowbackend.domain.shared.enums.Gender;
import pl.medflow.medflowbackend.domain.shared.enums.MedicalStaffPosition;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.shared.embedded.Address;
import pl.medflow.medflowbackend.domain.shared.embedded.RoomLocation;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserRegistrationRequest {
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String password;
    
    @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    private Role role;
    
    // Doctor specific fields
    private String licenseNumber;
    private String specialization;
    private String department;
    private RoomLocation office;
    private List<String> procedureIds;
    private Boolean acceptingPatients;
    
    // Patient specific fields
    private String nationalId;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Address address;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private Boolean consentGiven;
    private List<String> allergies;
    private List<String> medications;
    private List<String> chronicDiseases;
    private String familyDoctorId;
    
    // MedicalStaff specific fields
    private MedicalStaffPosition position;
    private RoomLocation assignedRoom;
} 