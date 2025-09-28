package pl.medflow.medflowbackend.domain.staff_management.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.identity.account.AccountService;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffRegistrationRequestDto;
import pl.medflow.medflowbackend.domain.staff_management.service.MedicalStaffService;

@Service
@RequiredArgsConstructor
public class MedicalStaffFacade {

    private final MedicalStaffService medicalStaffService;
    private final AccountService accountService;

    public void createMedicalStaff(MedicalStaffRegistrationRequestDto request) {
        var account = accountService.create(request.email(), request.password(), Role.MEDICAL_STAFF);
        medicalStaffService.create(request, account.getId());
    }

    public void deleteMedicalStaffById(String id) {
        medicalStaffService.delete(id);
        accountService.delete(id);
    }

    public void updatePasswordById(String id, String newRawPassword) {
        accountService.updatePasswordById(id, newRawPassword);
    }

    public void updatePasswordByEmail(String email, String newRawPassword) {
        accountService.updatePassword(email, newRawPassword);
    }

    public boolean verifyPassword(String email, String passwordHash) {
        return accountService.verifyPassword(email, passwordHash);
    }

    public void delete(String id) {
        medicalStaffService.delete(id);
        accountService.delete(id);
    }


}
