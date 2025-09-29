package pl.medflow.medflowbackend.domain.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import pl.medflow.medflowbackend.domain.identity.account.UserAccount;

public interface TokenService {

    String generateAccessToken(UserAccount user);

    String generateRefreshToken(UserAccount user, String jti);

    Jws<Claims> parse(String jwt);


    boolean isExpired(String jwt);

    String newJti();

    JwtTokens issueTokens(UserAccount user);

    JwtTokens rotateTokens(String refreshJwt);

    int getRefreshExpirationSeconds();

    String getCookieName();

    boolean isCookieSecure();

    String getCookieSameSite();

    String getCookiePath();
}
