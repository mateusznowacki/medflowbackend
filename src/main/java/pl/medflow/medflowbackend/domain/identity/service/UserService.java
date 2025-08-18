package pl.medflow.medflowbackend.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.user.dto.UserRegistrationRequest;
import pl.medflow.medflowbackend.domain.user.dto.UserResponse;
import pl.medflow.medflowbackend.domain.user.model.Doctor;
import pl.medflow.medflowbackend.domain.user.model.MedicalStaff;
import pl.medflow.medflowbackend.domain.user.model.Patient;
import pl.medflow.medflowbackend.domain.user.model.User;
import pl.medflow.medflowbackend.domain.user.model.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserResponse registerUser(UserRegistrationRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }
        
        User user = createUserFromRequest(request);
        User savedUser = userRepository.save(user);
        
        return mapToUserResponse(savedUser);
    }
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .toList();
    }
    
    public Optional<UserResponse> getUserById(String id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponse);
    }
    
    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToUserResponse);
    }
    
    public UserResponse updateUser(String id, UserRegistrationRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        User updatedUser = updateUserFromRequest(existingUser, request);
        User savedUser = userRepository.save(updatedUser);
        
        return mapToUserResponse(savedUser);
    }
    
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    
    private User createUserFromRequest(UserRegistrationRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        
        switch (request.getRole()) {
            case DOCTOR:
                return Doctor.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .password(encodedPassword)
                        .phoneNumber(request.getPhoneNumber())
                        .role(Role.DOCTOR)
                        .licenseNumber(request.getLicenseNumber())
                        .specialization(request.getSpecialization())
                        .department(request.getDepartment())
                        .office(request.getOffice())
                        .acceptingPatients(request.getAcceptingPatients() != null ? request.getAcceptingPatients() : true)
                        .build();
                        
            case PATIENT:
                return Patient.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .password(encodedPassword)
                        .phoneNumber(request.getPhoneNumber())
                        .role(Role.PATIENT)
                        .nationalId(request.getNationalId())
                        .gender(request.getGender())
                        .dateOfBirth(request.getDateOfBirth())
                        .address(request.getAddress())
                        .emergencyContactName(request.getEmergencyContactName())
                        .emergencyContactPhone(request.getEmergencyContactPhone())
                        .consentGiven(request.getConsentGiven() != null ? request.getConsentGiven() : false)
                        .allergies(request.getAllergies())
                        .medications(request.getMedications())
                        .chronicDiseases(request.getChronicDiseases())
                        .familyDoctorId(request.getFamilyDoctorId())
                        .build();
                        
            case MEDICAL_STAFF:
                return MedicalStaff.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .password(encodedPassword)
                        .phoneNumber(request.getPhoneNumber())
                        .role(Role.MEDICAL_STAFF)
                        .position(request.getPosition())
                        .department(request.getDepartment())
                        .assignedRoom(request.getAssignedRoom())
                        .licenseNumber(request.getLicenseNumber())
                        .build();
                        
            default:
                throw new RuntimeException("Unsupported role: " + request.getRole());
        }
    }
    
    private User updateUserFromRequest(User existingUser, UserRegistrationRequest request) {
        // Update common fields
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setPhoneNumber(request.getPhoneNumber());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
            existingUser.setPasswordUpdatedAt(Instant.now());
        }
        
        // Update role-specific fields
        if (existingUser instanceof Doctor && request.getRole() == Role.DOCTOR) {
            Doctor doctor = (Doctor) existingUser;
            doctor.setLicenseNumber(request.getLicenseNumber());
            doctor.setSpecialization(request.getSpecialization());
            doctor.setDepartment(request.getDepartment());
            doctor.setOffice(request.getOffice());
            if (request.getAcceptingPatients() != null) {
                doctor.setAcceptingPatients(request.getAcceptingPatients());
            }
        } else if (existingUser instanceof Patient && request.getRole() == Role.PATIENT) {
            Patient patient = (Patient) existingUser;
            patient.setNationalId(request.getNationalId());
            patient.setGender(request.getGender());
            patient.setDateOfBirth(request.getDateOfBirth());
            patient.setAddress(request.getAddress());
            patient.setEmergencyContactName(request.getEmergencyContactName());
            patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
            if (request.getConsentGiven() != null) {
                patient.setConsentGiven(request.getConsentGiven());
            }
            patient.setAllergies(request.getAllergies());
            patient.setMedications(request.getMedications());
            patient.setChronicDiseases(request.getChronicDiseases());
            patient.setFamilyDoctorId(request.getFamilyDoctorId());
        } else if (existingUser instanceof MedicalStaff && request.getRole() == Role.MEDICAL_STAFF) {
            MedicalStaff staff = (MedicalStaff) existingUser;
            staff.setPosition(request.getPosition());
            staff.setDepartment(request.getDepartment());
            staff.setAssignedRoom(request.getAssignedRoom());
            staff.setLicenseNumber(request.getLicenseNumber());
        }
        
        return existingUser;
    }
    
    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        response.setEmailVerified(user.isEmailVerified());
        response.setLastLoginAt(user.getLastLoginAt());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        
        if (user instanceof Doctor) {
            Doctor doctor = (Doctor) user;
            response.setLicenseNumber(doctor.getLicenseNumber());
            response.setSpecialization(doctor.getSpecialization());
            response.setDepartment(doctor.getDepartment());
            response.setOffice(doctor.getOffice());
            response.setAcceptingPatients(doctor.isAcceptingPatients());
        } else if (user instanceof Patient) {
            Patient patient = (Patient) user;
            response.setNationalId(patient.getNationalId());
            response.setGender(patient.getGender());
            response.setDateOfBirth(patient.getDateOfBirth());
            response.setAddress(patient.getAddress());
            response.setEmergencyContactName(patient.getEmergencyContactName());
            response.setEmergencyContactPhone(patient.getEmergencyContactPhone());
            response.setConsentGiven(patient.isConsentGiven());
            response.setTermsAcceptedAt(patient.getTermsAcceptedAt());
            response.setPrivacyPolicyAcceptedAt(patient.getPrivacyPolicyAcceptedAt());
            response.setAllergies(patient.getAllergies());
            response.setMedications(patient.getMedications());
            response.setChronicDiseases(patient.getChronicDiseases());
            response.setFamilyDoctorId(patient.getFamilyDoctorId());
        } else if (user instanceof MedicalStaff) {
            MedicalStaff staff = (MedicalStaff) user;
            response.setPosition(staff.getPosition());
            response.setAssignedRoom(staff.getAssignedRoom());
            response.setLicenseNumber(staff.getLicenseNumber());
        }
        
        return response;
    }
} 