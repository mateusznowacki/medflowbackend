package pl.medflow.medflowbackend.domain.staff_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.identity.auth.model.Account;
import pl.medflow.medflowbackend.domain.identity.auth.repository.AccountRepository;
import pl.medflow.medflowbackend.domain.shared.enums.MedicalStaffPosition;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.staff_management.MedicalStaffRegistrationRequest;
import pl.medflow.medflowbackend.domain.staff_management.MedicalStaffRepository;
import pl.medflow.medflowbackend.domain.staff_management.StaffResponse;
import pl.medflow.medflowbackend.domain.staff_management.model.MedicalStaff;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalStaffService {

    private final MedicalStaffRepository staffRepo;
    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;

    // CREATE / REGISTER
    public StaffResponse registerMedicalStaff(MedicalStaffRegistrationRequest request) {
        if (accountRepo.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Medical staff with this email already exists");
        }

        MedicalStaff staff = MedicalStaff.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.MEDICAL_STAFF)
                .position(request.getPosition())
                .department(request.getDepartment())
                .assignedRoom(request.getAssignedRoom())
                .licenseNumber(request.getLicenseNumber())
                .build();

        MedicalStaff saved = staffRepo.save(staff);

        // Create login account linked by the same ID
        accountRepo.save(Account.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .passwordHash(saved.getPassword())
                .role(Role.MEDICAL_STAFF)
                .build());

        return toResponse(saved);
    }

    // READ - all
    public List<StaffResponse> getAllMedicalStaff() {
        return staffRepo.findAll().stream().map(this::toResponse).toList();
    }

    // READ - by id
    public StaffResponse getMedicalStaffById(String id) {
        return staffRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Medical staff not found"));
    }

    // READ - by position
    public List<StaffResponse> getMedicalStaffByPosition(String position) {
        MedicalStaffPosition pos;
        try {
            pos = MedicalStaffPosition.valueOf(position.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid position: " + position);
        }
        return staffRepo.findAll().stream()
                .filter(s -> pos.equals(s.getPosition()))
                .map(this::toResponse)
                .toList();
    }

    // READ - by department
    public List<StaffResponse> getMedicalStaffByDepartment(String department) {
        return staffRepo.findAll().stream()
                .filter(s -> department.equalsIgnoreCase(s.getDepartment()))
                .map(this::toResponse)
                .toList();
    }

    // UPDATE
    public StaffResponse updateMedicalStaff(String id, MedicalStaffRegistrationRequest request) {
        MedicalStaff existing = staffRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medical staff not found"));

        if (request.getFirstName() != null) existing.setFirstName(request.getFirstName());
        if (request.getLastName() != null) existing.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) existing.setPhoneNumber(request.getPhoneNumber());
        if (request.getPosition() != null) existing.setPosition(request.getPosition());
        if (request.getDepartment() != null) existing.setDepartment(request.getDepartment());
        if (request.getAssignedRoom() != null) existing.setAssignedRoom(request.getAssignedRoom());
        if (request.getLicenseNumber() != null) existing.setLicenseNumber(request.getLicenseNumber());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String encoded = passwordEncoder.encode(request.getPassword());
            existing.setPassword(encoded);
            // update account password as well
            accountRepo.findById(id).ifPresent(acc -> {
                acc.setPasswordHash(encoded);
                accountRepo.save(acc);
            });
        }

        MedicalStaff saved = staffRepo.save(existing);
        return toResponse(saved);
    }

    // DELETE
    public void deleteMedicalStaff(String id) {
        MedicalStaff staff = staffRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medical staff not found"));
        staffRepo.delete(staff);
        accountRepo.deleteById(id);
    }

    private StaffResponse toResponse(MedicalStaff s) {
        return new StaffResponse(
                s.getId(),
                s.getFirstName(),
                s.getLastName(),
                s.getEmail(),
                s.getPhoneNumber(),
                s.getPosition(),
                s.getDepartment(),
                s.getAssignedRoom(),
                s.getLicenseNumber()
        );
    }
}