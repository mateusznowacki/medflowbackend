package pl.medflow.medflowbackend.domain.patient_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.identity.dto.PatientRegistrationRequest;
import pl.medflow.medflowbackend.domain.identity.dto.UserResponse;
import pl.medflow.medflowbackend.domain.patient_management.model.Patient;
import pl.medflow.medflowbackend.domain.identity.repository.UserRepository;
import pl.medflow.medflowbackend.domain.identity.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserResponse registerPatient(PatientRegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Patient with this email already exists");
        }
        
        Patient patient = Patient.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
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
        
        Patient savedPatient = userRepository.save(patient);
        return mapToUserResponse(savedPatient);
    }
    
    public List<UserResponse> getAllPatients() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Patient)
                .map(this::mapToUserResponse)
                .toList();
    }
    
    public Optional<UserResponse> getPatientById(String id) {
        return userRepository.findById(id)
                .filter(user -> user instanceof Patient)
                .map(this::mapToUserResponse);
    }
    
    public List<UserResponse> getPatientsByDoctor(String doctorId) {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Patient)
                .map(user -> (Patient) user)
                .filter(patient -> doctorId.equals(patient.getFamilyDoctorId()))
                .map(this::mapToUserResponse)
                .toList();
    }
    
    public UserResponse updatePatient(String id, PatientRegistrationRequest request) {
        Patient existingPatient = (Patient) userRepository.findById(id)
                .filter(user -> user instanceof Patient)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        existingPatient.setFirstName(request.getFirstName());
        existingPatient.setLastName(request.getLastName());
        existingPatient.setPhoneNumber(request.getPhoneNumber());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingPatient.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        existingPatient.setNationalId(request.getNationalId());
        existingPatient.setGender(request.getGender());
        existingPatient.setDateOfBirth(request.getDateOfBirth());
        existingPatient.setAddress(request.getAddress());
        existingPatient.setEmergencyContactName(request.getEmergencyContactName());
        existingPatient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        if (request.getConsentGiven() != null) {
            existingPatient.setConsentGiven(request.getConsentGiven());
        }
        existingPatient.setAllergies(request.getAllergies());
        existingPatient.setMedications(request.getMedications());
        existingPatient.setChronicDiseases(request.getChronicDiseases());
        existingPatient.setFamilyDoctorId(request.getFamilyDoctorId());
        
        Patient savedPatient = userRepository.save(existingPatient);
        return mapToUserResponse(savedPatient);
    }
    
    public void deletePatient(String id) {
        Patient patient = (Patient) userRepository.findById(id)
                .filter(user -> user instanceof Patient)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        userRepository.delete(patient);
    }
    
    private UserResponse mapToUserResponse(Patient patient) {
        UserResponse response = new UserResponse();
        response.setId(patient.getId());
        response.setFirstName(patient.getFirstName());
        response.setLastName(patient.getLastName());
        response.setEmail(patient.getEmail());
        response.setPhoneNumber(patient.getPhoneNumber());
        response.setRole(patient.getRole());
        response.setActive(patient.isActive());
        response.setEmailVerified(patient.isEmailVerified());
        response.setLastLoginAt(patient.getLastLoginAt());
        response.setCreatedAt(patient.getCreatedAt());
        response.setUpdatedAt(patient.getUpdatedAt());
        
        // Patient specific fields
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
        
        return response;
    }
} 