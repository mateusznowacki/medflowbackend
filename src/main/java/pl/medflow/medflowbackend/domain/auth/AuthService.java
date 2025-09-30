package pl.medflow.medflowbackend.domain.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.identity.account.UserAccountService;
import pl.medflow.medflowbackend.domain.token.TokenService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final UserAccountService userAccountService;
    private final PasswordEncoder passwordEncoder;

    public LoginResult login(LoginRequest req) {
        var user = userAccountService.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        return tokenService.issueTokens(user);
    }

    public LoginResult refresh(String refreshJwtFromCookie) {
        return tokenService.refresh(refreshJwtFromCookie);
    }

    // Wariant wygodny dla kontrolera – bez znajomości nazwy ciasteczka
    public LoginResult refresh(HttpServletRequest request) {
        return tokenService.refreshFromRequest(request);
    }

    public ResponseCookie logout(String refreshJwtFromCookie) {
        return tokenService.logout(refreshJwtFromCookie);
    }
}
