package pl.medflow.medflowbackend.domain.doctor_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.medflow.medflowbackend.domain.doctor_management.DoctorResponse;
import pl.medflow.medflowbackend.domain.doctor_management.model.DoctorRegistrationRequest;
import pl.medflow.medflowbackend.domain.doctor_management.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    
    private final DoctorService doctorService;

    @PostMapping("/register")
    public ResponseEntity<DoctorResponse> registerDoctor(@Valid @RequestBody DoctorRegistrationRequest request) {
        try {
            DoctorResponse doctor = doctorService.registerDoctor(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping
    public ResponseEntity<java.util.List<DoctorResponse>> getAllDoctors() {
        java.util.List<DoctorResponse> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable String id) {
        try {
            return ResponseEntity.ok(doctorService.getDoctorById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<java.util.List<DoctorResponse>> getDoctorsBySpecialization(@PathVariable String specialization) {
        java.util.List<DoctorResponse> doctors = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.ok(doctors);
    }
//

    @GetMapping("/department/{department}")
    public ResponseEntity<java.util.List<DoctorResponse>> getDoctorsByDepartment(@PathVariable String department) {
        java.util.List<DoctorResponse> doctors = doctorService.getDoctorsByDepartment(department);
        return ResponseEntity.ok(doctors);
    }
    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(@PathVariable String id,
                                                       @Valid @RequestBody DoctorRegistrationRequest request) {
        try {
            DoctorResponse doctor = doctorService.updateDoctor(id, request);
            return ResponseEntity.ok(doctor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable String id) {
        try {
            doctorService.deleteDoctor(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 