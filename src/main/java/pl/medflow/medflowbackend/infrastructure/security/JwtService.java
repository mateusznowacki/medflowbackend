// security/JwtService.java
package pl.medflow.medflowbackend.domain.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.users.User;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties props;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(props.getSecret().getBytes());
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getAccess().getExpirationMinutes() * 60L);

        return Jwts.builder()
                .setIssuer(props.getAccess().getIssuer())
                .setSubject(user.getId())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(Map.of(
                        "email", user.getEmail(),
                        "role",  user.getRole().name()
                ))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(User user, String jti) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getRefresh().getExpirationDays() * 24L * 3600L);

        return Jwts.builder()
                .setIssuer(props.getRefresh().getIssuer())
                .setSubject(user.getId())
                .setId(jti) // <- jti = id dokumentu refresh w Mongo
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("typ", "refresh")
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parse(String jwt) throws JwtException {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(jwt);
    }

    public String getSubject(String jwt) { return parse(jwt).getPayload().getSubject(); }
    public String getJti(String jwt) { return parse(jwt).getPayload().getId(); }
    public boolean isExpired(String jwt) { return parse(jwt).getPayload().getExpiration().before(new Date()); }

    public String newJti() { return UUID.randomUUID().toString(); }
}
