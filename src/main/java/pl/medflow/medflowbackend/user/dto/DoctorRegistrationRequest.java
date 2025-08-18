package pl.medflow.medflowbackend.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import pl.medflow.medflowbackend.shared.embedded.RoomLocation;

import java.util.List;

@Data
public class DoctorRegistrationRequest {
    
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
    
    @NotBlank
    private String licenseNumber;
    
    private String specialization;
    private String department;
    private RoomLocation office;
    private List<String> procedureIds;
    private Boolean acceptingPatients = true;
} 