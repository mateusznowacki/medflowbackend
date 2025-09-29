package pl.medflow.medflowbackend.domain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.auth.exceptions.InvalidCredentialsException;
import pl.medflow.medflowbackend.domain.auth.exceptions.InvalidTokenException;
import pl.medflow.medflowbackend.domain.auth.exceptions.MissingRefreshTokenException;
import pl.medflow.medflowbackend.domain.auth.exceptions.UserNotFoundException;
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
                .orElseThrow(InvalidCredentialsException::new);
        boolean verifiedPassword = userAccountService.verifyPassword(req.email(), req.password());
        if (!verifiedPassword) {
            throw new InvalidCredentialsException();
        }

        JwtTokens tokens = tokenService.issueTokens(user);
        ResponseCookie cookie = buildRefreshCookie(tokens.refreshToken(),
                tokenService.getRefreshExpirationSeconds());
        LoginResponse body = LoginResponse.bearer(tokens.accessToken(), tokens.expiresInSeconds());
        return new LoginResult(body, cookie);
    }

    public LoginResult refresh(String refreshJwtFromCookie) {
        if (refreshJwtFromCookie == null || refreshJwtFromCookie.isBlank()) {
            throw new MissingRefreshTokenException();
        }

        String subject;
        try {
            var claims = tokenService.parse(refreshJwtFromCookie).getPayload();
            subject = claims.getSubject();
        } catch (RuntimeException ex) {
            throw new InvalidTokenException();
        }

        var user = userAccountService.getById(subject)
                .orElseThrow(UserNotFoundException::new);

        JwtTokens tokens = tokenService.rotateTokens(refreshJwtFromCookie, user);

        ResponseCookie cookie = buildRefreshCookie(
                tokens.refreshToken(),
                tokenService.getRefreshExpirationSeconds()
        );

        LoginResponse body = LoginResponse.bearer(
                tokens.accessToken(),
                tokens.expiresInSeconds()
        );

        return new LoginResult(body, cookie);
    }

    public ResponseCookie logout() {
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

    public String getCookieName() {
        return tokenService.getCookieName();
    }
}
