package pl.medflow.medflowbackend.domain.identity.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.token.JwtService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UserAccount create(String email, String rawPassword, Role role) {
        accountRepository.findByEmail(email).ifPresent(account -> {
            throw new IllegalArgumentException("Account with email " + email + " already exists.");
        });

        var account = UserAccount.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        return accountRepository.save(account);
    }

    @Transactional
    public void changeEmail(String currentEmail, String newEmail) {
        if (currentEmail.equals(newEmail)) {
            throw new IllegalArgumentException("New email must be different from the current email.");
        }
        if (accountRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Account with email " + newEmail + " already exists.");
        }
        var account = accountRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("Account with email " + currentEmail + " not found."));
        account.setEmail(newEmail);
        accountRepository.save(account);
    }

    @Transactional
    public void changePassword(String id, String newPassword) {
        accountRepository.findById(id).ifPresentOrElse(
                account -> {
                    account.setPasswordHash(passwordEncoder.encode(newPassword));
                    accountRepository.save(account);
                },
                () -> {
                    throw new IllegalArgumentException("Account with id " + id + " not found.");
                }
        );
    }

    public boolean verifyPassword(String email, String rawPassword) {
        var passwordHash = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Account with email " + email + " not found."))
                .getPasswordHash();
        return passwordEncoder.matches(rawPassword, passwordHash);
    }

    @Transactional
    public void delete(String id) {
        accountRepository.findById(id)
                .ifPresentOrElse(
                        accountRepository::delete,
                        () -> {
                            throw new IllegalArgumentException("Account with id " + id + " not found.");
                        }
                );
    }

    public Optional<UserAccount> getById(@NotBlank String id) {
        return accountRepository.findById(id);
    }

    public Optional<UserAccount> findByEmail(@Email @NotBlank String email) {
        return accountRepository.findByEmail(email);
    }

}