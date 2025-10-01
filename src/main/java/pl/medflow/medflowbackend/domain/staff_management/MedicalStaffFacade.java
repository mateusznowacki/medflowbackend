package pl.medflow.medflowbackend.domain.staff_management;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.medflow.medflowbackend.domain.identity.account.UserAccount;
import pl.medflow.medflowbackend.domain.identity.account.UserAccountService;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffProfileResponse;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffRegistrationRequest;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffSummaryResponse;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffUpdateRequest;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalStaffFacade {

    private final MedicalStaffService medicalStaffService;
    private final UserAccountService userAccountService;

    @Transactional
    public void createMedicalStaff(@Valid MedicalStaffRegistrationRequest request) {
        userAccountService.findByEmail(request.email()).ifPresent(acc -> {
            throw new IllegalArgumentException("Account with email " + request.email() + " already exists.");
        });

        var account = userAccountService.create(request.email(),
                                                request.firstName(),
                                                request.lastName(),
                                                request.password(),
                                                Role.MEDICAL_STAFF);

        medicalStaffService.create(request, account.getId());
    }

    // Idempotent registration: if account/email exists, return existing profile instead of error
    @Transactional
    public MedicalStaffProfileResponse createMedicalStaffIfNotExists(@Valid MedicalStaffRegistrationRequest request) {
        Optional<UserAccount> existing = userAccountService.findByEmail(request.email());
        if (existing.isPresent()) {
            String accountId = existing.get().getId();
            // If staff record might not exist, ensure it's created
            try {
                medicalStaffService.get(accountId);
            } catch (IllegalArgumentException ex) {
                medicalStaffService.create(request, accountId);
            }
            return getProfile(accountId);
        }
        var account = userAccountService.create(request.email(),
                request.firstName(),
                request.lastName(),
                request.password(),
                Role.MEDICAL_STAFF);
        medicalStaffService.create(request, account.getId());
        return getProfile(account.getId());
    }

    @Transactional
    public void delete(String id) {
        medicalStaffService.delete(id);
        userAccountService.delete(id);
    }


    @Transactional
    public MedicalStaffSummaryResponse update (MedicalStaffUpdateRequest request, String accountId){
      return medicalStaffService.update(request, accountId);
    }

    public MedicalStaffSummaryResponse getByLicenseNumber(String licenseNumber) {
        return medicalStaffService.getByLicenseNumber(licenseNumber);
    }

    public MedicalStaffSummaryResponse getById(String accountId){
        return medicalStaffService.get(accountId);
    }

    public List<MedicalStaffSummaryResponse> getAll(){
        return medicalStaffService.getAll();
    }

    public MedicalStaffSummaryResponse getByEmail(String email){
        var account = userAccountService.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("Account with email " + email + " not found"));
        return medicalStaffService.get(account.getId());
    }

    // Combined profile retrieval
    public MedicalStaffProfileResponse getProfile(String accountId) {
        var account = userAccountService.getRequiredById(accountId);
        var staff = medicalStaffService.get(accountId);
        return new MedicalStaffProfileResponse(
                account.getId(),
                account.getEmail(),
                account.getFirstName(),
                account.getLastName(),
                account.getRole(),
                staff.phoneNumber(),
                true, // status mapped as active; summary lacks flag, assume true
                staff.position(),
                staff.department(),
                staff.assignedRoom() != null ? staff.assignedRoom().toString() : null,
                staff.licenseNumber()
        );
    }

    public List<MedicalStaffProfileResponse> getProfiles(List<String> accountIds) {
        // fetch accounts map
        Map<String, UserAccount> accounts = accountIds.stream()
                .map(userAccountService::getRequiredById)
                .collect(Collectors.toMap(UserAccount::getId, Function.identity()));
        return accountIds.stream()
                .map(id -> {
                    var staff = medicalStaffService.get(id);
                    var acc = accounts.get(id);
                    return new MedicalStaffProfileResponse(
                            acc.getId(), acc.getEmail(), acc.getFirstName(), acc.getLastName(), acc.getRole(),
                            staff.phoneNumber(), true,
                            staff.position(), staff.department(),
                            staff.assignedRoom() != null ? staff.assignedRoom().toString() : null,
                            staff.licenseNumber()
                    );
                })
                .toList();
    }

    // Simple search with pagination and optional position filter
    public List<MedicalStaffProfileResponse> search(String query, Optional<MedicalStaffPosition> positionOpt, int page, int size) {
        String q = query == null ? "" : query.trim().toLowerCase();
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }
        var staffEntities = medicalStaffService.getAllEntities();
        var filtered = staffEntities.stream()
                .filter(staff -> positionOpt.map(p -> p.equals(staff.getPosition())).orElse(true))
                .map(staff -> {
                    var acc = userAccountService.getRequiredById(staff.getId());
                    boolean matches = q.isEmpty()
                            || (acc.getFirstName() != null && acc.getFirstName().toLowerCase().contains(q))
                            || (acc.getLastName() != null && acc.getLastName().toLowerCase().contains(q))
                            || (acc.getEmail() != null && acc.getEmail().toLowerCase().contains(q))
                            || (staff.getLicenseNumber() != null && staff.getLicenseNumber().toLowerCase().contains(q))
                            || (staff.getDepartment() != null && staff.getDepartment().toLowerCase().contains(q));
                    if (!matches) return null;
                    return new MedicalStaffProfileResponse(
                            acc.getId(), acc.getEmail(), acc.getFirstName(), acc.getLastName(), acc.getRole(),
                            staff.getPhoneNumber(), staff.isActive(), staff.getPosition(), staff.getDepartment(),
                            staff.getAssignedRoom() != null ? staff.getAssignedRoom().toString() : null,
                            staff.getLicenseNumber()
                    );
                })
                .filter(p -> p != null)
                .sorted(Comparator.comparing(MedicalStaffProfileResponse::lastName)
                        .thenComparing(MedicalStaffProfileResponse::firstName))
                .toList();
        int from = Math.min(filtered.size(), page * size);
        int to = Math.min(filtered.size(), from + size);
        if (from >= to) return List.of();
        return filtered.subList(from, to);
    }

    @Transactional
    public void updateContact(String accountId, String newEmail, Optional<String> phone) {
        if (newEmail != null && !newEmail.isBlank()) {
            userAccountService.updateEmailById(accountId, newEmail);
        }
        phone.filter(p -> !p.isBlank()).ifPresent(p -> medicalStaffService.updatePhone(accountId, p));
    }

    @Transactional
    public void updateName(String accountId, String firstName, String lastName) {
        userAccountService.updateName(accountId, firstName, lastName);
        // If we had any staff display cache, we could refresh here.
    }

    @Transactional
    public MedicalStaffSummaryResponse updateProfessionalData(String accountId, MedicalStaffUpdateRequest request) {
        return medicalStaffService.update(request, accountId);
    }

    @Transactional
    public void changeStatus(String accountId, boolean active, Optional<String> reason) {
        // Map StaffStatus to active boolean; auditing omitted
        medicalStaffService.setActive(accountId, active);
    }

    public void assignToFacility(String accountId, String facilityId) {
        throw new UnsupportedOperationException("Facility assignments are not supported by current model");
    }

    public void unassignFromFacility(String accountId, String facilityId) {
        throw new UnsupportedOperationException("Facility assignments are not supported by current model");
    }

    public void setPositions(String accountId, Set<MedicalStaffPosition> positions) {
        throw new UnsupportedOperationException("Multiple positions per facility not supported by current model");
    }

    @Transactional
    public void changePassword(String accountId, String newPassword) {
        userAccountService.changePassword(accountId, newPassword);
    }
}
