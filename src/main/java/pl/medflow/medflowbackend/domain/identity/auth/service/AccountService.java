package pl.medflow.medflowbackend.domain.identity.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.medflow.medflowbackend.domain.identity.auth.model.Account;
import pl.medflow.medflowbackend.domain.identity.auth.repository.AccountRepository;
import pl.medflow.medflowbackend.domain.shared.enums.Role;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Account create(String email, String rawPassword, Role role) {
        var account = Account.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        return accountRepository.save(account);
    }

    public void updatePassword(String email, String newRawPassword) {
        var accountOpt = accountRepository.findByEmail(email);
        if (accountOpt.isPresent()) {
            var account = accountOpt.get();
            account.setPasswordHash(passwordEncoder.encode(newRawPassword));
            accountRepository.save(account);
        } else {
            throw new IllegalArgumentException("Account with email " + email + " not found.");
        }
    }

    public void updatePasswordById(String id, String newRawPassword) {
        var accountOpt = accountRepository.findById(id);
        if (accountOpt.isPresent()) {
            var account = accountOpt.get();
            account.setPasswordHash(passwordEncoder.encode(newRawPassword));
            accountRepository.save(account);
        } else {
            throw new IllegalArgumentException("Account with id " + id + " not found.");
        }
    }

    public void updateEmail(String currentEmail, String newEmail) {
        var accountOpt = accountRepository.findByEmail(currentEmail);
        if (accountOpt.isPresent()) {
            var account = accountOpt.get();
            account.setEmail(newEmail);
            accountRepository.save(account);
        } else {
            throw new IllegalArgumentException("Account with email " + currentEmail + " not found.");
        }
    }

    public boolean verifyPassword(String account, String passwordHash) {
        var password = accountRepository.findByEmail(account)
                .orElseThrow(() -> new IllegalArgumentException("Account with email " + account + " not found."))
                .getPasswordHash();
        return passwordEncoder.matches(passwordHash, password);
    }

    public void delete(String id) {
        accountRepository.deleteById(id);
    }
}