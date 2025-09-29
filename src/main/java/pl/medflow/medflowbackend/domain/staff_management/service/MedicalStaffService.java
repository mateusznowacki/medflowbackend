package pl.medflow.medflowbackend.domain.staff_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicalStaffService {

//    private final MedicalStaffRepository medicalStaffRepo;
//
//    public MedicalStaffResponseDto create(MedicalStaffRegistrationRequestDto request, String id) {
//
//        MedicalStaff staff = MedicalStaff.builder()
//                .id(id)
//                .role(Role.MEDICAL_STAFF)
//                .firstName(request.firstName())
//                .lastName(request.lastName())
//                .email(request.email())
//                .position(request.position())
//                .department(request.department())
//                .assignedRoom(request.assignedRoom())
//                .licenseNumber(request.licenseNumber())
//                .build();
//
//        MedicalStaff saved = medicalStaffRepo.save(staff);
//        return toResponse(saved);
//    }
//
//    public List<MedicalStaffResponseDto> getAllMedicalStaff() {
//        return medicalStaffRepo.findAll().stream().map(this::toResponse).toList();
//    }
//
//
//    public MedicalStaffResponseDto getMedicalStaffById(String id) {
//        return medicalStaffRepo.findById(id).map(this::toResponse)
//                .orElseThrow(() -> new IllegalArgumentException("Medical staff not found"));
//    }
//
//
//    public MedicalStaffResponseDto getMedicalStaffByEmail(String email) {
//        return medicalStaffRepo.findAll().stream()
//                .filter(s -> email.equalsIgnoreCase(s.getEmail()))
//                .findFirst()
//                .map(this::toResponse)
//                .orElseThrow(() -> new IllegalArgumentException("Medical staff not found"));
//    }
//
//    public List<MedicalStaffResponseDto> getMedicalStaffByPosition(String position) {
//        MedicalStaffPosition pos;
//        try {
//            pos = MedicalStaffPosition.valueOf(position.toUpperCase());
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Invalid position: " + position);
//        }
//        return medicalStaffRepo.findAll().stream()
//                .filter(s -> pos.equals(s.getPosition()))
//                .map(this::toResponse)
//                .toList();
//    }
//
//
//    public List<MedicalStaffResponseDto> getMedicalStaffByDepartment(String department) {
//        return medicalStaffRepo.findAll().stream()
//                .filter(s -> department.equalsIgnoreCase(s.getDepartment()))
//                .map(this::toResponse)
//                .toList();
//    }
//
////    public MedicalStaffResponseDto updateMedicalStaff(String id, MedicalStaffUpdateRequestDto request) {
////        MedicalStaff existing = medicalStaffRepo.findById(id)
////                .orElseThrow(() -> new IllegalArgumentException("Medical staff not found"));
////
////        if (request.email() != null) {
////            if (!request.email().equalsIgnoreCase(existing.getEmail()) && accountRepo.existsByEmail(request.email())) {
////                throw new IllegalArgumentException("Email already in use");
////            }
////            existing.setEmail(request.email());
////            Account acc = accountRepo.findById(id)
////                    .orElseThrow(() -> new IllegalArgumentException("Associated account not found"));
////            acc.setEmail(request.email());
////            accountRepo.save(acc);
////        }
////        if (request.phoneNumber() != null) existing.setPhoneNumber(request.phoneNumber());
////        if (request.position() != null) existing.setPosition(request.position());
////        if (request.department() != null) existing.setDepartment(request.department());
////        if (request.assignedRoom() != null) existing.setAssignedRoom(request.assignedRoom());
////        if (request.licenseNumber() != null) existing.setLicenseNumber(request.licenseNumber());
////
////        MedicalStaff saved = medicalStaffRepo.save(existing);
////        return toResponse(saved);
////    }
//
//
//    public void delete(String id) {
//        medicalStaffRepo.deleteById(id);
//
//    }
//
//    private MedicalStaffResponseDto toResponse(MedicalStaff medicalStaff) {
//        return new MedicalStaffResponseDto(
//                medicalStaff.getId(),
//                medicalStaff.getFirstName(),
//                medicalStaff.getLastName(),
//                medicalStaff.getEmail(),
//                medicalStaff.getPhoneNumber(),
//                medicalStaff.getPosition(),
//                medicalStaff.getDepartment(),
//                medicalStaff.getAssignedRoom(),
//                medicalStaff.getLicenseNumber()
//        );
//    }
}