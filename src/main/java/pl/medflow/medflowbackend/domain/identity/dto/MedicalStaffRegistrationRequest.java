package pl.medflow.medflowbackend.domain.identity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import pl.medflow.medflowbackend.domain.shared.enums.MedicalStaffPosition;
import pl.medflow.medflowbackend.domain.shared.embedded.RoomLocation;

@Data
public class MedicalStaffRegistrationRequest {
    
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
    
    private MedicalStaffPosition position;
    private String department;
    private RoomLocation assignedRoom;
    private String licenseNumber;
} 