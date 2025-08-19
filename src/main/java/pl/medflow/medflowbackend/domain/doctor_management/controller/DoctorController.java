package pl.medflow.medflowbackend.domain.doctor_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.medflow.medflowbackend.domain.identity.dto.DoctorRegistrationRequest;
import pl.medflow.medflowbackend.domain.identity.dto.UserResponse;
import pl.medflow.medflowbackend.domain.doctor_management.service.DoctorService;
import pl.medflow.medflowbackend.domain.identity.model.User;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    
    private final DoctorService doctorService;
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerDoctor(@Valid @RequestBody DoctorRegistrationRequest request) {
        try {
            UserResponse doctor = doctorService.registerDoctor(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllDoctors() {
        List<UserResponse> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getDoctorById(@PathVariable String id) {
        Optional<UserResponse> doctor = doctorService.getDoctorById(id);
        return doctor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<UserResponse>> getDoctorsBySpecialization(@PathVariable String specialization) {
        List<UserResponse> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/department/{department}")
    public ResponseEntity<List<UserResponse>> getDoctorsByDepartment(@PathVariable String department) {
        List<UserResponse> doctors = doctorService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(doctors);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateDoctor(@PathVariable String id, 
                                                   @Valid @RequestBody DoctorRegistrationRequest request) {
        try {
            UserResponse doctor = doctorService.updateDoctor(id, request);
            return ResponseEntity.ok(doctor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        try {
            doctorService.deleteDoctor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 