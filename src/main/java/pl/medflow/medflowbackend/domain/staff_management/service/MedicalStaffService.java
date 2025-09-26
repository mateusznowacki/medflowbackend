package pl.medflow.medflowbackend.domain.staff_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.identity.auth.model.Account;
import pl.medflow.medflowbackend.domain.identity.auth.repository.AccountRepository;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffRegistrationRequestDto;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffResponseDto;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffUpdateRequestDto;
import pl.medflow.medflowbackend.domain.staff_management.model.MedicalStaff;
import pl.medflow.medflowbackend.domain.staff_management.model.MedicalStaffPosition;
import pl.medflow.medflowbackend.domain.staff_management.repository.MedicalStaffRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MedicalStaffService {

    private final MedicalStaffRepository staffRepo;
    private final AccountRepository accountRepo;

    // CREATE / REGISTER
    public MedicalStaffResponseDto registerMedicalStaff(MedicalStaffRegistrationRequestDto request) {
        if (accountRepo.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Medical staff with this email already exists");
        }

        String id = UUID.randomUUID().toString();

        Account account = Account.builder()
                .id(id)
                .email(request.email())
                .passwordHash(request.password())
                .role(Role.MEDICAL_STAFF)
                .build();
        accountRepo.save(account);

        MedicalStaff staff = MedicalStaff.builder()
                .id(id)
                .role(Role.MEDICAL_STAFF)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .position(request.position())
                .department(request.department())
                .assignedRoom(request.assignedRoom())
                .licenseNumber(request.licenseNumber())
                .build();

        MedicalStaff saved = staffRepo.save(staff);
        return toResponse(saved);
    }

    // READ - all
    public List<MedicalStaffResponseDto> getAllMedicalStaff() {
        return staffRepo.findAll().stream().map(this::toResponse).toList();
    }

    // READ - by id
    public MedicalStaffResponseDto getMedicalStaffById(String id) {
        return staffRepo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Medical staff not found"));
    }

    // READ - by email
    public MedicalStaffResponseDto getMedicalStaffByEmail(String email) {
        return staffRepo.findAll().stream()
                .filter(s -> email.equalsIgnoreCase(s.getEmail()))
                .findFirst()
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Medical staff not found"));
    }

    // READ - by position
    public List<MedicalStaffResponseDto> getMedicalStaffByPosition(String position) {
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
    public List<MedicalStaffResponseDto> getMedicalStaffByDepartment(String department) {
        return staffRepo.findAll().stream()
                .filter(s -> department.equalsIgnoreCase(s.getDepartment()))
                .map(this::toResponse)
                .toList();
    }

    public MedicalStaffResponseDto updateMedicalStaff(String id, MedicalStaffUpdateRequestDto request) {
        MedicalStaff existing = staffRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medical staff not found"));

        if(request.email() != null) {
            if (!request.email().equalsIgnoreCase(existing.getEmail()) && accountRepo.existsByEmail(request.email())) {
                throw new IllegalArgumentException("Email already in use");
            }
            existing.setEmail(request.email());
            Account acc = accountRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Associated account not found"));
            acc.setEmail(request.email());
            accountRepo.save(acc);
        }
        if(request.phoneNumber() != null) existing.setPhoneNumber(request.phoneNumber());
        if(request.position() != null) existing.setPosition(request.position());
        if(request.department() != null) existing.setDepartment(request.department());
        if(request.assignedRoom() != null) existing.setAssignedRoom(request.assignedRoom());
        if(request.licenseNumber() != null) existing.setLicenseNumber(request.licenseNumber());

        MedicalStaff saved = staffRepo.save(existing);
        return toResponse(saved);
    }

    // CHANGE PASSWORD (Account only)
    public void changePassword(String id, String newPasswordHash) {
        Account acc = accountRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        acc.setPasswordHash(newPasswordHash);
        accountRepo.save(acc);
    }

    // DELETE
    public void deleteMedicalStaff(String id) {
        MedicalStaff staff = staffRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medical staff not found"));
        staffRepo.delete(staff);
        accountRepo.deleteById(id);
    }

    private MedicalStaffResponseDto toResponse(MedicalStaff medicalStaff) {
        return new MedicalStaffResponseDto(
                medicalStaff.getId(),
                medicalStaff.getFirstName(),
                medicalStaff.getLastName(),
                medicalStaff.getEmail(),
                medicalStaff.getPhoneNumber(),
                medicalStaff.getPosition(),
                medicalStaff.getDepartment(),
                medicalStaff.getAssignedRoom(),
                medicalStaff.getLicenseNumber()
        );
    }
}