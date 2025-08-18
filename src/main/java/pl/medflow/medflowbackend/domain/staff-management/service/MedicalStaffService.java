package pl.medflow.medflowbackend.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.user.dto.MedicalStaffRegistrationRequest;
import pl.medflow.medflowbackend.domain.user.dto.UserResponse;
import pl.medflow.medflowbackend.domain.user.model.MedicalStaff;
import pl.medflow.medflowbackend.domain.user.model.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicalStaffService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserResponse registerMedicalStaff(MedicalStaffRegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Medical staff with this email already exists");
        }
        
        MedicalStaff staff = MedicalStaff.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.MEDICAL_STAFF)
                .position(request.getPosition())
                .department(request.getDepartment())
                .assignedRoom(request.getAssignedRoom())
                .licenseNumber(request.getLicenseNumber())
                .build();
        
        MedicalStaff savedStaff = userRepository.save(staff);
        return mapToUserResponse(savedStaff);
    }
    
    public List<UserResponse> getAllMedicalStaff() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof MedicalStaff)
                .map(this::mapToUserResponse)
                .toList();
    }
    
    public Optional<UserResponse> getMedicalStaffById(String id) {
        return userRepository.findById(id)
                .filter(user -> user instanceof MedicalStaff)
                .map(this::mapToUserResponse);
    }
    
    public List<UserResponse> getMedicalStaffByPosition(String position) {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof MedicalStaff)
                .map(user -> (MedicalStaff) user)
                .filter(staff -> position.equals(staff.getPosition().name()))
                .map(this::mapToUserResponse)
                .toList();
    }
    
    public List<UserResponse> getMedicalStaffByDepartment(String department) {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof MedicalStaff)
                .map(user -> (MedicalStaff) user)
                .filter(staff -> department.equals(staff.getDepartment()))
                .map(this::mapToUserResponse)
                .toList();
    }
    
    public UserResponse updateMedicalStaff(String id, MedicalStaffRegistrationRequest request) {
        MedicalStaff existingStaff = (MedicalStaff) userRepository.findById(id)
                .filter(user -> user instanceof MedicalStaff)
                .orElseThrow(() -> new RuntimeException("Medical staff not found"));
        
        existingStaff.setFirstName(request.getFirstName());
        existingStaff.setLastName(request.getLastName());
        existingStaff.setPhoneNumber(request.getPhoneNumber());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingStaff.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        existingStaff.setPosition(request.getPosition());
        existingStaff.setDepartment(request.getDepartment());
        existingStaff.setAssignedRoom(request.getAssignedRoom());
        existingStaff.setLicenseNumber(request.getLicenseNumber());
        
        MedicalStaff savedStaff = userRepository.save(existingStaff);
        return mapToUserResponse(savedStaff);
    }
    
    public void deleteMedicalStaff(String id) {
        MedicalStaff staff = (MedicalStaff) userRepository.findById(id)
                .filter(user -> user instanceof MedicalStaff)
                .orElseThrow(() -> new RuntimeException("Medical staff not found"));
        userRepository.delete(staff);
    }
    
    private UserResponse mapToUserResponse(MedicalStaff staff) {
        UserResponse response = new UserResponse();
        response.setId(staff.getId());
        response.setFirstName(staff.getFirstName());
        response.setLastName(staff.getLastName());
        response.setEmail(staff.getEmail());
        response.setPhoneNumber(staff.getPhoneNumber());
        response.setRole(staff.getRole());
        response.setActive(staff.isActive());
        response.setEmailVerified(staff.isEmailVerified());
        response.setLastLoginAt(staff.getLastLoginAt());
        response.setCreatedAt(staff.getCreatedAt());
        response.setUpdatedAt(staff.getUpdatedAt());
        
        // MedicalStaff specific fields
        response.setPosition(staff.getPosition());
        response.setDepartment(staff.getDepartment());
        response.setAssignedRoom(staff.getAssignedRoom());
        response.setLicenseNumber(staff.getLicenseNumber());
        
        return response;
    }
} 