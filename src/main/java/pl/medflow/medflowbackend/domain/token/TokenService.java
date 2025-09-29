package pl.medflow.medflowbackend.domain.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import pl.medflow.medflowbackend.domain.identity.account.UserAccount;

public interface TokenService {

    String generateAccessToken(UserAccount user);

    String generateRefreshToken(UserAccount user, String jti);

    Jws<Claims> parse(String jwt);

    String getSubject(String jwt);

    String getJti(String jwt);

    boolean isExpired(String jwt);

    String newJti();

    Tokens issueTokens(UserAccount user);

    Tokens rotateTokens(String refreshJwt);
}
