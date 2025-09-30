package pl.medflow.medflowbackend.domain.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.identity.account.UserAccountService;
import pl.medflow.medflowbackend.domain.token.TokenService;

@Service
@RequiredArgsConstructor
public class AuthService {

private final TokenService tokenService;
    private final UserAccountService userAccountService;

    public LoginResult login(LoginRequest req) {
        var user = userAccountService.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
        return tokenService.login(user, req.password());
    }

    public LoginResult refresh(String refreshJwtFromCookie) {
        return tokenService.refresh(refreshJwtFromCookie);
    }

    // Wariant wygodny dla kontrolera – bez znajomości nazwy ciasteczka
    public LoginResult refresh(HttpServletRequest request) {
        return tokenService.refreshFromRequest(request);
    }

    public ResponseCookie logout() {
        return tokenService.logout();
    }
}
