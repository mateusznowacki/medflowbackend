package pl.medflow.medflowbackend.domain.identity.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.medflow.medflowbackend.domain.doctor_management.repository.DoctorRepository;
import pl.medflow.medflowbackend.domain.identity.auth.dto.LoginRequest;
import pl.medflow.medflowbackend.domain.identity.auth.dto.LoginResponse;
import pl.medflow.medflowbackend.domain.identity.auth.model.Account;
import pl.medflow.medflowbackend.domain.identity.auth.model.RefreshTokenDocument;
import pl.medflow.medflowbackend.domain.identity.auth.repository.AccountRepository;
import pl.medflow.medflowbackend.domain.identity.auth.repository.RefreshTokenRepository;
import pl.medflow.medflowbackend.domain.identity.user.model.User;
import pl.medflow.medflowbackend.domain.patient_management.repository.PatientRepository;
import pl.medflow.medflowbackend.domain.shared.enums.Role;
import pl.medflow.medflowbackend.domain.staff_management.repository.MedicalStaffRepository;
import pl.medflow.medflowbackend.infrastructure.security.JwtProperties;
import pl.medflow.medflowbackend.infrastructure.security.JwtService;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final MedicalStaffRepository staffRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshRepo;
    private final JwtProperties props;

    // ==== LOGIN ====
    public record LoginResult(LoginResponse body, ResponseCookie refreshCookie) {}

    public LoginResult login(LoginRequest req) {
        Account acc = accountRepo.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), acc.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        User user = loadUserFor(acc);

        // Access token (permissions wstrzykiwane wewnątrz JwtService)
        String access = jwtService.generateAccessToken(user);

        // Refresh token
        String jti = jwtService.newJti();
        Instant refreshExp = Instant.now().plus(Duration.ofDays(props.getRefresh().getExpirationDays()));
        refreshRepo.save(RefreshTokenDocument.builder()
                .id(jti)
                .userId(user.getId())
                .expiresAt(refreshExp)
                .revoked(false)
                .build());

        String refresh = jwtService.generateRefreshToken(user, jti);

        return new LoginResult(
                toLoginResponse(user, access),
                buildRefreshCookie(refresh, refreshMaxAgeSeconds())
        );
    }

    // ==== REFRESH ====
    public record RefreshResult(LoginResponse body, ResponseCookie refreshCookie) {}

    public RefreshResult refresh(String refreshJwtFromCookie) {
        if (!StringUtils.hasText(refreshJwtFromCookie)) {
            throw new IllegalArgumentException("Missing refresh token");
        }

        var claims = jwtService.parse(refreshJwtFromCookie).getPayload();
        String userId = claims.getSubject();
        String jti = claims.getId();

        RefreshTokenDocument doc = refreshRepo.findByIdAndRevokedFalse(jti)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token revoked or not found"));

        doc.setRevoked(true);
        refreshRepo.save(doc);

        Account acc = accountRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        User user = loadUserFor(acc);

        String access = jwtService.generateAccessToken(user);

        String newJti = jwtService.newJti();
        Instant refreshExp = Instant.now().plus(Duration.ofDays(props.getRefresh().getExpirationDays()));
        refreshRepo.save(RefreshTokenDocument.builder()
                .id(newJti)
                .userId(user.getId())
                .expiresAt(refreshExp)
                .revoked(false)
                .build());

        doc.setReplacedBy(newJti);
        refreshRepo.save(doc);

        String newRefresh = jwtService.generateRefreshToken(user, newJti);

        return new RefreshResult(
                toLoginResponse(user, access),
                buildRefreshCookie(newRefresh, refreshMaxAgeSeconds())
        );
    }

    // ==== LOGOUT ====
    public ResponseCookie logout(String refreshJwtFromCookie) {
        try {
            if (StringUtils.hasText(refreshJwtFromCookie)) {
                String jti = jwtService.getJti(refreshJwtFromCookie);
                refreshRepo.findById(jti).ifPresent(token -> {
                    token.setRevoked(true);
                    refreshRepo.save(token);
                });
            }
        } catch (Exception ignored) { }
        return buildRefreshCookie("", 0);
    }

    // ==== helpers ====
    private User loadUserFor(Account acc) {
        String id = acc.getId();
        Role role = acc.getRole();
        return switch (role) {
            case DOCTOR -> doctorRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
            case PATIENT -> patientRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Patient not found"));
            case MEDICAL_STAFF -> staffRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Staff not found"));
            default -> throw new IllegalArgumentException("Unsupported role for profile: " + role);
        };
    }

 private LoginResponse toLoginResponse(User user, String accessToken) {
    return LoginResponse.builder()
            .accessToken(accessToken)
            .expiresIn(props.getAccess().getExpirationMinutes() * 60L)
            .build();
}


    private int refreshMaxAgeSeconds() {
        return (int) Duration.ofDays(props.getRefresh().getExpirationDays()).getSeconds();
    }

    private ResponseCookie buildRefreshCookie(String value, int maxAgeSeconds) {
        return ResponseCookie.from(props.getCookie().getName(), value)
                .httpOnly(true)
                .secure(props.getCookie().isSecure())
                .sameSite(props.getCookie().getSameSite())
                .path(props.getCookie().getPath())
                .maxAge(maxAgeSeconds)
                .build();
    }
}
