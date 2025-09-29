package pl.medflow.medflowbackend.domain.staff_management.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.identity.account.UserAccountService;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.staff_management.dto.MedicalStaffRegistrationRequestDto;
import pl.medflow.medflowbackend.domain.staff_management.service.MedicalStaffService;

@Service
@RequiredArgsConstructor
public class MedicalStaffFacade {

//    private final MedicalStaffService medicalStaffService;
//    private final UserAccountService userAccountService;
//
//    public void createMedicalStaff(MedicalStaffRegistrationRequestDto request) {
//        var account = userAccountService.create(request.email(), request.password(), Role.MEDICAL_STAFF);
//        medicalStaffService.create(request, account.getId());
//    }
//
//    public void deleteMedicalStaffById(String id) {
//        medicalStaffService.delete(id);
//        userAccountService.delete(id);
//    }
//
//    public void updatePasswordById(String id, String newRawPassword) {
//        userAccountService.updatePasswordById(id, newRawPassword);
//    }
//
//    public void updatePasswordByEmail(String email, String newRawPassword) {
//        userAccountService.updatePassword(email, newRawPassword);
//    }
//
//    public boolean verifyPassword(String email, String passwordHash) {
//        return userAccountService.verifyPassword(email, passwordHash);
//    }
//
//    public void delete(String id) {
//        medicalStaffService.delete(id);
//        userAccountService.delete(id);
//    }


}
