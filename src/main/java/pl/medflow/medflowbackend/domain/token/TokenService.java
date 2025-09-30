package pl.medflow.medflowbackend.domain.token;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import pl.medflow.medflowbackend.domain.auth.LoginResult;
import pl.medflow.medflowbackend.domain.identity.account.UserAccount;

public interface TokenService {

    // Issue tokens for already authenticated user
    LoginResult issueTokens(UserAccount user);

    // Refresh rotation with jti tracking
    LoginResult refresh(String refreshJwt);

    LoginResult refreshFromRequest(HttpServletRequest request);

    // Logout should revoke current refresh token by jti and expire cookie
    ResponseCookie logout(String refreshJwtFromCookie);

    // Convenience: logout using request cookies without exposing cookie name
    ResponseCookie logoutFromRequest(HttpServletRequest request);

    AccessClaims verifyAccessToken(String accessJwt);
}
