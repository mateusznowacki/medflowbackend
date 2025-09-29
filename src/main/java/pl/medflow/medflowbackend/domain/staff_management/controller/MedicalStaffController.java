package pl.medflow.medflowbackend.domain.staff_management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/medical-staff")
@RequiredArgsConstructor
public class MedicalStaffController {
//
//    private final MedicalStaffService medicalStaffService;
//
//    @PostMapping("/register")
//    public ResponseEntity<MedicalStaffResponseDto> registerMedicalStaff(@Valid @RequestBody MedicalStaffRegistrationRequestDto request) {
//        try {
//            MedicalStaffResponseDto staff = medicalStaffService.create(request);
//            return ResponseEntity.status(HttpStatus.CREATED).body(staff);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @GetMapping
//    public ResponseEntity<List<MedicalStaffResponseDto>> getAllMedicalStaff() {
//        List<MedicalStaffResponseDto> staff = medicalStaffService.getAllMedicalStaff();
//        return ResponseEntity.ok(staff);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<MedicalStaffResponseDto> getMedicalStaffById(@PathVariable String id) {
//        try {
//            return ResponseEntity.ok(medicalStaffService.getMedicalStaffById(id));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @GetMapping("/position/{position}")
//    public ResponseEntity<List<MedicalStaffResponseDto>> getMedicalStaffByPosition(@PathVariable String position) {
//        List<MedicalStaffResponseDto> staff = medicalStaffService.getMedicalStaffByPosition(position);
//        return ResponseEntity.ok(staff);
//    }
//
//    @GetMapping("/department/{department}")
//    public ResponseEntity<List<MedicalStaffResponseDto>> getMedicalStaffByDepartment(@PathVariable String department) {
//        List<MedicalStaffResponseDto> staff = medicalStaffService.getMedicalStaffByDepartment(department);
//        return ResponseEntity.ok(staff);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<MedicalStaffResponseDto> updateMedicalStaff(@PathVariable String id,
//                                                                      @Valid @RequestBody MedicalStaffUpdateRequestDto request) {
//        try {
//            MedicalStaffResponseDto staff = medicalStaffService.updateMedicalStaff(id, request);
//            return ResponseEntity.ok(staff);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteMedicalStaff(@PathVariable String id) {
//        try {
//            medicalStaffService.delete(id);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
}