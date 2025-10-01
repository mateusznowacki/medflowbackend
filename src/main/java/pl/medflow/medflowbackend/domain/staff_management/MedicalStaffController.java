package pl.medflow.medflowbackend.domain.staff_management;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.medflow.medflowbackend.domain.staff_management.dto.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/medical-staff")
@RequiredArgsConstructor
public class MedicalStaffController {

    private final MedicalStaffFacade medicalStaffFacade;

    // Registration
    @PostMapping("/register")
    public void register(@Valid @RequestBody MedicalStaffRegistrationRequest request) {
        medicalStaffFacade.createMedicalStaff(request);
    }

    @PostMapping("/register/idempotent")
    public MedicalStaffProfileResponse registerIfNotExists(@Valid @RequestBody MedicalStaffRegistrationRequest request) {
        return medicalStaffFacade.createMedicalStaffIfNotExists(request);
    }

    // Retrieval (summaries)
    @GetMapping("/{accountId}")
    public MedicalStaffSummaryResponse getById(@PathVariable String accountId) {
        return medicalStaffFacade.getById(accountId);
    }

    @GetMapping
    public List<MedicalStaffSummaryResponse> getAll() {
        return medicalStaffFacade.getAll();
    }

    @GetMapping("/by-email")
    public MedicalStaffSummaryResponse getByEmail(@RequestParam String email) {
        return medicalStaffFacade.getByEmail(email);
    }

    @GetMapping("/by-license/{licenseNumber}")
    public MedicalStaffSummaryResponse getByLicense(@PathVariable String licenseNumber) {
        return medicalStaffFacade.getByLicenseNumber(licenseNumber);
    }

    // Profiles
    @GetMapping("/profiles/{accountId}")
    public MedicalStaffProfileResponse getProfile(@PathVariable String accountId) {
        return medicalStaffFacade.getProfile(accountId);
    }

    @PostMapping("/profiles/batch")
    public List<MedicalStaffProfileResponse> getProfiles(@RequestBody List<String> accountIds) {
        return medicalStaffFacade.getProfiles(accountIds);
    }

    // Search
    @GetMapping("/search")
    public List<MedicalStaffProfileResponse> search(
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "position", required = false) MedicalStaffPosition position,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return medicalStaffFacade.search(query, Optional.ofNullable(position), page, size);
    }

    // Updates
    @PatchMapping("/{accountId}/contact")
    public void updateContact(@PathVariable String accountId, @Valid @RequestBody UpdateContactRequest request) {
        medicalStaffFacade.updateContact(accountId, request.newEmail(), Optional.ofNullable(request.phone()));
    }

    @PatchMapping("/{accountId}/name")
    public void updateName(@PathVariable String accountId, @Valid @RequestBody UpdateNameRequest request) {
        medicalStaffFacade.updateName(accountId, request.firstName(), request.lastName());
    }

    @PatchMapping("/{accountId}/professional")
    public MedicalStaffSummaryResponse updateProfessional(@PathVariable String accountId, @Valid @RequestBody MedicalStaffUpdateRequest request) {
        return medicalStaffFacade.updateProfessionalData(accountId, request);
    }

    @PatchMapping("/{accountId}/status")
    public void changeStatus(@PathVariable String accountId, @Valid @RequestBody ChangeStatusRequest request) {
        medicalStaffFacade.changeStatus(accountId, request.active(), Optional.ofNullable(request.reason()));
    }

    @PostMapping("/{accountId}/password")
    public void changePassword(@PathVariable String accountId, @Valid @RequestBody ChangePasswordRequest request) {
        medicalStaffFacade.changePassword(accountId, request.newPassword());
    }

    // Delete
    @DeleteMapping("/{accountId}")
    public void delete(@PathVariable String accountId) {
        medicalStaffFacade.delete(accountId);
    }
}