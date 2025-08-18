package pl.medflow.medflowbackend.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.medflow.medflowbackend.domain.user.dto.DoctorRegistrationRequest;
import pl.medflow.medflowbackend.domain.user.dto.UserResponse;
import pl.medflow.medflowbackend.domain.user.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerDoctor(@Valid @RequestBody DoctorRegistrationRequest request) {
        try {
            UserResponse doctor = userService.registerDoctor(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllDoctors() {
        List<UserResponse> doctors = userService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getDoctorById(@PathVariable String id) {
        Optional<UserResponse> doctor = userService.getDoctorById(id);
        return doctor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<UserResponse>> getDoctorsBySpecialization(@PathVariable String specialization) {
        List<UserResponse> doctors = userService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }
    
    @GetMapping("/department/{department}")
    public ResponseEntity<List<UserResponse>> getDoctorsByDepartment(@PathVariable String department) {
        List<UserResponse> doctors = userService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(doctors);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateDoctor(@PathVariable String id, 
                                                   @Valid @RequestBody DoctorRegistrationRequest request) {
        try {
            UserResponse doctor = userService.updateDoctor(id, request);
            return ResponseEntity.ok(doctor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        try {
            userService.deleteDoctor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 