package pl.medflow.medflowbackend.domain.staff_management;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.medflow.medflowbackend.domain.identity.account.UserAccountService;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffRegistrationRequest;

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

    @Transactional
    public void delete(String id) {
        medicalStaffService.delete(id);
        userAccountService.delete(id);
    }


}
