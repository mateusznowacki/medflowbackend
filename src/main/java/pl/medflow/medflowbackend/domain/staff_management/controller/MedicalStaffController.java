package pl.medflow.medflowbackend.domain.staff_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medical-staff")
@RequiredArgsConstructor
public class MedicalStaffController {
    
//    private final MedicalStaffService medicalStaffService;
//
//    @PostMapping("/register")
//    public ResponseEntity<UserResponse> registerMedicalStaff(@Valid @RequestBody MedicalStaffRegistrationRequest request) {
//        try {
//            UserResponse staff = medicalStaffService.registerMedicalStaff(request);
//            return ResponseEntity.status(HttpStatus.CREATED).body(staff);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
////    @GetMapping
////    public ResponseEntity<List<UserResponse>> getAllMedicalStaff() {
////        List<UserResponse> staff = medicalStaffService.getAllMedicalStaff();
////        return ResponseEntity.ok(staff);
////    }
//
////    @GetMapping("/{id}")
////    public ResponseEntity<UserResponse> getMedicalStaffById(@PathVariable String id) {
////        Optional<UserResponse> staff = medicalStaffService.getMedicalStaffById(id);
////        return staff.map(ResponseEntity::ok)
////                .orElse(ResponseEntity.notFound().build());
////    }
////
//    @GetMapping("/position/{position}")
//    public ResponseEntity<List<UserResponse>> getMedicalStaffByPosition(@PathVariable String position) {
//        List<UserResponse> staff = medicalStaffService.getMedicalStaffByPosition(position);
//        return ResponseEntity.ok(staff);
//    }
//
//    @GetMapping("/department/{department}")
//    public ResponseEntity<List<UserResponse>> getMedicalStaffByDepartment(@PathVariable String department) {
//        List<UserResponse> staff = medicalStaffService.getMedicalStaffByDepartment(department);
//        return ResponseEntity.ok(staff);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<UserResponse> updateMedicalStaff(@PathVariable String id,
//                                                       @Valid @RequestBody MedicalStaffRegistrationRequest request) {
//        try {
//            UserResponse staff = medicalStaffService.updateMedicalStaff(id, request);
//            return ResponseEntity.ok(staff);
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteMedicalStaff(@PathVariable String id) {
//        try {
//            medicalStaffService.deleteMedicalStaff(id);
//            return ResponseEntity.noContent().build();
//        } catch (RuntimeException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
} 