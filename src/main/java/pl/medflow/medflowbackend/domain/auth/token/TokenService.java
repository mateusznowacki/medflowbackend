package pl.medflow.medflowbackend.domain.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import pl.medflow.medflowbackend.domain.shared.user.BasicUserDto;

public interface TokenService {

    String generateAccessToken(BasicUserDto user);

    String generateRefreshToken(BasicUserDto user, String jti);

    Jws<Claims> parse(String jwt);

    String getSubject(String jwt);

    String getJti(String jwt);

    boolean isExpired(String jwt);

    String newJti();

    Tokens issueTokens(BasicUserDto user);

    Tokens rotateTokens(String refreshJwt);
}
