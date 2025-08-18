package pl.medflow.medflowbackend.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.medflow.medflowbackend.domain.user.dto.MedicalStaffRegistrationRequest;
import pl.medflow.medflowbackend.domain.user.dto.UserResponse;
import pl.medflow.medflowbackend.domain.user.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-staff")
@RequiredArgsConstructor
public class MedicalStaffController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerMedicalStaff(@Valid @RequestBody MedicalStaffRegistrationRequest request) {
        try {
            UserResponse staff = userService.registerMedicalStaff(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(staff);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllMedicalStaff() {
        List<UserResponse> staff = userService.getAllMedicalStaff();
        return ResponseEntity.ok(staff);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getMedicalStaffById(@PathVariable String id) {
        Optional<UserResponse> staff = userService.getMedicalStaffById(id);
        return staff.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/position/{position}")
    public ResponseEntity<List<UserResponse>> getMedicalStaffByPosition(@PathVariable String position) {
        List<UserResponse> staff = userService.getMedicalStaffByPosition(position);
        return ResponseEntity.ok(staff);
    }
    
    @GetMapping("/department/{department}")
    public ResponseEntity<List<UserResponse>> getMedicalStaffByDepartment(@PathVariable String department) {
        List<UserResponse> staff = userService.getMedicalStaffByDepartment(department);
        return ResponseEntity.ok(staff);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateMedicalStaff(@PathVariable String id, 
                                                       @Valid @RequestBody MedicalStaffRegistrationRequest request) {
        try {
            UserResponse staff = userService.updateMedicalStaff(id, request);
            return ResponseEntity.ok(staff);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalStaff(@PathVariable String id) {
        try {
            userService.deleteMedicalStaff(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 