package pl.medflow.medflowbackend.domain.token;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import pl.medflow.medflowbackend.domain.auth.LoginResult;
import pl.medflow.medflowbackend.domain.identity.account.UserAccount;

public interface TokenService {

    LoginResult login(UserAccount user, String rawPassword);

    LoginResult refresh(String refreshJwt);

     LoginResult refreshFromRequest(HttpServletRequest request);

    ResponseCookie logout();

     AccessClaims verifyAccessToken(String accessJwt);
}
