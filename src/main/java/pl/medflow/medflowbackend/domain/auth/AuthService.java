package pl.medflow.medflowbackend.domain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.identity.account.UserAccountService;
import pl.medflow.medflowbackend.domain.token.JwtTokens;
import pl.medflow.medflowbackend.domain.token.TokenService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final UserAccountService userAccountService;

    public LoginResult login(LoginRequest req) {
            var user = userAccountService.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        boolean verifiedPassword = userAccountService.verifyPassword(req.email(), req.password());
        if (!verifiedPassword) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        JwtTokens tokens = tokenService.issueTokens(user);
        ResponseCookie cookie = buildRefreshCookie(tokens.refreshToken(),
               tokenService.getRefreshExpirationSeconds());
        LoginResponse body = LoginResponse.bearer(tokens.accessToken(), tokens.expiresInSeconds());
        return new LoginResult(body, cookie);
    }

    public LoginResult refresh(String refreshJwtFromCookie) {
        if (refreshJwtFromCookie == null || refreshJwtFromCookie.isBlank()) {
            throw new IllegalArgumentException("Missing refresh token");
        }
        JwtTokens tokens = tokenService.rotateTokens(refreshJwtFromCookie);
        ResponseCookie cookie = buildRefreshCookie(tokens.refreshToken(),
                tokenService.getRefreshExpirationSeconds());
        LoginResponse body = LoginResponse.bearer(tokens.accessToken(), tokens.expiresInSeconds());
        return new LoginResult(body, cookie);
    }

    public ResponseCookie logout(String refreshJwtFromCookie) {
        return buildRefreshCookie("", 0);
    }

private ResponseCookie buildRefreshCookie(String value, int maxAgeSeconds) {
    return ResponseCookie.from(tokenService.getCookieName(), value)
            .httpOnly(true)
            .secure(tokenService.isCookieSecure())
            .sameSite(tokenService.getCookieSameSite())
            .path(tokenService.getCookiePath())
            .maxAge(maxAgeSeconds)
            .build();
}
}
