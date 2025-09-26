package pl.medflow.medflowbackend.domain.identity.auth.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.identity.auth.model.Account;
import pl.medflow.medflowbackend.domain.identity.auth.service.AccountService;

@Service
@RequiredArgsConstructor
public class AccountFacade {

    private final AccountService accountService;

    public Account create(String email, String rawPassword, String role) {
        return accountService.create(email, rawPassword, role);
    }

    public void updatePassword(String email, String newRawPassword) {
        accountService.updatePassword(email, newRawPassword);
    }

    public void updatePasswordById(String id, String newRawPassword) {
        accountService.updatePasswordById(id, newRawPassword);
    }

    public void updateEmail(String currentEmail, String newEmail) {
        accountService.updateEmail(currentEmail, newEmail);
    }

    public boolean verifyPassword(String id, String endcryptedPassword) {
        return accountService.verifyPassword(id, endcryptedPassword);
    }

    public void deleteByEmail(String email) {
        accountService.deleteByEmail(email);
    }

    public void deleteById(String id) {
        accountService.deleteById(id);
    }
}
