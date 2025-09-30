package pl.medflow.medflowbackend.domain.staff_management;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffRegistrationRequest;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffSummaryResponse;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffUpdateRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalStaffService {

    private final MedicalStaffRepository medicalStaffRepository;
    
    @Transactional
    public void create(MedicalStaffRegistrationRequest request, String accountId) {
        if (request.licenseNumber() != null && !request.licenseNumber().isBlank()
                && medicalStaffRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new IllegalArgumentException("License number already in use");
        }
        if (request.position() == null) {
            throw new IllegalArgumentException("Position is required");
        }

        var staff = MedicalStaff.builder()
                .id(accountId)
                .phoneNumber(request.phoneNumber())
                .position(request.position())
                .department(request.department())
                .assignedRoom(request.assignedRoom())
                .licenseNumber(request.licenseNumber())
                .active(true)
                .build();

        medicalStaffRepository.save(staff);
    }
    
    public MedicalStaffSummaryResponse get(String accountId) {
         var staff = medicalStaffRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Medical staff with id " + accountId + " not found"));
         
         return mapToResponse(staff);
    }
    
    public List<MedicalStaffSummaryResponse> getAll(){
        return medicalStaffRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    public MedicalStaffSummaryResponse getByLicenseNumber(String licenseNumber) {
        var staff= medicalStaffRepository.findByLicenseNumber(licenseNumber).orElseThrow(() ->
                new IllegalArgumentException("Medical staff with license number " + licenseNumber + " not found"));
        
        return mapToResponse(staff);
    }
           

    @Transactional
    public MedicalStaffSummaryResponse update(MedicalStaffUpdateRequest request, String accountId) {
        var staff = medicalStaffRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Medical staff with id " + accountId + " not found"));

        if (request.phoneNumber() != null) {
            staff.setPhoneNumber(request.phoneNumber());
        }
        if (request.position() != null) {
            staff.setPosition(request.position());
        }
        if (request.department() != null) {
            staff.setDepartment(request.department());
        }
        if (request.assignedRoom() != null) {
            staff.setAssignedRoom(request.assignedRoom());
        }
        if (request.licenseNumber() != null
                && !request.licenseNumber().equals(staff.getLicenseNumber())) {

            medicalStaffRepository.findByLicenseNumber(request.licenseNumber())
                    .filter(existing -> !existing.getId().equals(staff.getId()))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException(
                                "Medical staff with license number " + request.licenseNumber() + " already exists"
                        );
                    });

            staff.setLicenseNumber(request.licenseNumber());
        }

        var saved = medicalStaffRepository.save(staff);

        return mapToResponse(saved);
    }
    
    @Transactional
    public void delete(String accountId) {
        medicalStaffRepository.findById(accountId)
                .ifPresentOrElse(
                        medicalStaffRepository::delete,
                        () -> {
                            throw new IllegalArgumentException("Medical staff with id " + accountId + " not found.");
                        }
                );
    }
    
    private MedicalStaffSummaryResponse mapToResponse(MedicalStaff staff) {
        return new MedicalStaffSummaryResponse(
                staff.getPhoneNumber(),
                staff.getPosition(),
                staff.getDepartment(),
                staff.getAssignedRoom(),
                staff.getLicenseNumber()
        );
    }
    
    
    
}