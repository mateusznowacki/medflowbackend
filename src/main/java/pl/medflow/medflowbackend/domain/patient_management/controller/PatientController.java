package pl.medflow.medflowbackend.domain.patient_management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.medflow.medflowbackend.domain.patient_management.PatientRegistrationRequest;
import pl.medflow.medflowbackend.domain.identity.auth.dto.UserResponse;
import pl.medflow.medflowbackend.domain.patient_management.service.PatientService;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    
    private final PatientService patientService;
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {
        try {
            UserResponse patient = patientService.registerPatient(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(patient);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
//    @GetMapping
//    public ResponseEntity<List<UserResponse>> getAllPatients() {
//        List<UserResponse> patients = patientService.getAllPatients();
//        return ResponseEntity.ok(patients);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<UserResponse> getPatientById(@PathVariable String id) {
//        Optional<UserResponse> patient = patientService.getPatientById(id);
//        return patient.map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<UserResponse>> getPatientsByDoctor(@PathVariable String doctorId) {
        List<UserResponse> patients = patientService.getPatientsByDoctor(doctorId);
        return ResponseEntity.ok(patients);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updatePatient(@PathVariable String id, 
                                                   @Valid @RequestBody PatientRegistrationRequest request) {
        try {
            UserResponse patient = patientService.updatePatient(id, request);
            return ResponseEntity.ok(patient);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable String id) {
        try {
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 