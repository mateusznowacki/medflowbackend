// src/main/java/pl/medflow/medflowbackend/auth/AuthService.java
package pl.medflow.medflowbackend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import pl.medflow.medflowbackend.auth.dto.BasicUser;
import pl.medflow.medflowbackend.auth.dto.LoginRequest;
import pl.medflow.medflowbackend.auth.dto.LoginResponse;
import pl.medflow.medflowbackend.users.User;
import pl.medflow.medflowbackend.users.UserRepository;
import pl.medflow.medflowbackend.security.JwtProperties;
import pl.medflow.medflowbackend.security.JwtService;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshRepo;
    private final JwtProperties props;

    public record LoginResult(LoginResponse body, ResponseCookie refreshCookie) {}

    public LoginResult login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Access
        String access = jwtService.generateAccessToken(user);

        // Refresh (persisted)
        String jti = jwtService.newJti();
        Instant refreshExp = Instant.now().plus(Duration.ofDays(props.getRefresh().getExpirationDays()));

        refreshRepo.save(RefreshTokenDocument.builder()
                .id(jti)
                .userId(user.getId())
                .expiresAt(refreshExp)
                .revoked(false)
                .build());

        String refresh = jwtService.generateRefreshToken(user, jti);
        ResponseCookie cookie = buildRefreshCookie(
                refresh,
                (int) Duration.ofDays(props.getRefresh().getExpirationDays()).getSeconds()
        );

        LoginResponse body = LoginResponse.builder()
                .accessToken(access)
                .expiresIn(props.getAccess().getExpirationMinutes() * 60L)
                .user(BasicUser.builder()
                        .id(user.getId())
                        .role(user.getRole())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .build())
                .build();

        return new LoginResult(body, cookie);
    }

    public record RefreshResult(LoginResponse body, ResponseCookie refreshCookie) {}

    public RefreshResult refresh(String refreshJwtFromCookie) {
        if (!StringUtils.hasText(refreshJwtFromCookie)) {
            throw new IllegalArgumentException("Missing refresh token");
        }

        // Walidacja JWT (sygnatura, exp)
        var claims = jwtService.parse(refreshJwtFromCookie).getPayload();
        String userId = claims.getSubject();
        String jti = claims.getId();

        // Sprawdzenie w Mongo czy nie revoked
        RefreshTokenDocument doc = refreshRepo.findByIdAndRevokedFalse(jti)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token revoked or not found"));

        // Rotacja: revoke starego
        doc.setRevoked(true);
        refreshRepo.save(doc);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Nowy access
        String access = jwtService.generateAccessToken(user);

        // Nowy refresh
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
        ResponseCookie cookie = buildRefreshCookie(
                newRefresh,
                (int) Duration.ofDays(props.getRefresh().getExpirationDays()).getSeconds()
        );

        LoginResponse body = LoginResponse.builder()
                .accessToken(access)
                .expiresIn(props.getAccess().getExpirationMinutes() * 60L)
                .user(BasicUser.builder()
                        .id(user.getId())
                        .role(user.getRole())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .build())
                .build();

        return new RefreshResult(body, cookie);
    }

    public ResponseCookie logout(String refreshJwtFromCookie) {
        try {
            if (StringUtils.hasText(refreshJwtFromCookie)) {
                String jti = jwtService.getJti(refreshJwtFromCookie);
                refreshRepo.findById(jti).ifPresent(token -> {
                    token.setRevoked(true);
                    refreshRepo.save(token);
                });
            }
        } catch (Exception ignored) { /* brak tokenu/parsowania – i tak czyścimy cookie */ }

        // wyczyść cookie
        return buildRefreshCookie("", 0);
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
