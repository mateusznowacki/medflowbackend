package pl.medflow.medflowbackend.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.shared.enums.Role;
import pl.medflow.medflowbackend.user.dto.DoctorRegistrationRequest;
import pl.medflow.medflowbackend.user.dto.UserResponse;
import pl.medflow.medflowbackend.user.model.Doctor;
import pl.medflow.medflowbackend.user.model.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserResponse registerDoctor(DoctorRegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Doctor with this email already exists");
        }
        
        Doctor doctor = Doctor.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.DOCTOR)
                .licenseNumber(request.getLicenseNumber())
                .specialization(request.getSpecialization())
                .department(request.getDepartment())
                .office(request.getOffice())
                .acceptingPatients(request.getAcceptingPatients() != null ? request.getAcceptingPatients() : true)
                .build();
        
        Doctor savedDoctor = userRepository.save(doctor);
        return mapToUserResponse(savedDoctor);
    }
    
    public List<UserResponse> getAllDoctors() {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Doctor)
                .map(this::mapToUserResponse)
                .toList();
    }
    
    public Optional<UserResponse> getDoctorById(String id) {
        return userRepository.findById(id)
                .filter(user -> user instanceof Doctor)
                .map(this::mapToUserResponse);
    }
    
    public List<UserResponse> getDoctorsBySpecialization(String specialization) {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Doctor)
                .map(user -> (Doctor) user)
                .filter(doctor -> specialization.equals(doctor.getSpecialization()))
                .map(this::mapToUserResponse)
                .toList();
    }
    
    public List<UserResponse> getDoctorsByDepartment(String department) {
        return userRepository.findAll().stream()
                .filter(user -> user instanceof Doctor)
                .map(user -> (Doctor) user)
                .filter(doctor -> department.equals(doctor.getDepartment()))
                .map(this::mapToUserResponse)
                .toList();
    }
    
    public UserResponse updateDoctor(String id, DoctorRegistrationRequest request) {
        Doctor existingDoctor = (Doctor) userRepository.findById(id)
                .filter(user -> user instanceof Doctor)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        existingDoctor.setFirstName(request.getFirstName());
        existingDoctor.setLastName(request.getLastName());
        existingDoctor.setPhoneNumber(request.getPhoneNumber());
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existingDoctor.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        existingDoctor.setLicenseNumber(request.getLicenseNumber());
        existingDoctor.setSpecialization(request.getSpecialization());
        existingDoctor.setDepartment(request.getDepartment());
        existingDoctor.setOffice(request.getOffice());
        if (request.getAcceptingPatients() != null) {
            existingDoctor.setAcceptingPatients(request.getAcceptingPatients());
        }
        
        Doctor savedDoctor = userRepository.save(existingDoctor);
        return mapToUserResponse(savedDoctor);
    }
    
    public void deleteDoctor(String id) {
        Doctor doctor = (Doctor) userRepository.findById(id)
                .filter(user -> user instanceof Doctor)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        userRepository.delete(doctor);
    }
    
    private UserResponse mapToUserResponse(Doctor doctor) {
        UserResponse response = new UserResponse();
        response.setId(doctor.getId());
        response.setFirstName(doctor.getFirstName());
        response.setLastName(doctor.getLastName());
        response.setEmail(doctor.getEmail());
        response.setPhoneNumber(doctor.getPhoneNumber());
        response.setRole(doctor.getRole());
        response.setActive(doctor.isActive());
        response.setEmailVerified(doctor.isEmailVerified());
        response.setLastLoginAt(doctor.getLastLoginAt());
        response.setCreatedAt(doctor.getCreatedAt());
        response.setUpdatedAt(doctor.getUpdatedAt());
        
        // Doctor specific fields
        response.setLicenseNumber(doctor.getLicenseNumber());
        response.setSpecialization(doctor.getSpecialization());
        response.setDepartment(doctor.getDepartment());
        response.setOffice(doctor.getOffice());
        response.setAcceptingPatients(doctor.isAcceptingPatients());
        
        return response;
    }
} 