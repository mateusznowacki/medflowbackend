// infrastructure/security/JwtService.java
package pl.medflow.medflowbackend.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.identity.user.model.User;
import pl.medflow.medflowbackend.infrastructure.secrets.AwsSecrets;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties props;
    private final AwsSecrets secrets;

    // Prosty cache kluczy (w razie rotacji możesz przeładować na żądanie)
    private volatile PrivateKey cachedPrivateKey;
    private volatile PublicKey  cachedPublicKey;

    private PrivateKey privateKey() {
        PrivateKey k = cachedPrivateKey;
        if (k != null) return k;
        String pem = notBlank(secrets.getJwtPrivateKeyPem(), "JWT private key is empty");
        cachedPrivateKey = k = readPrivateKeyFromPem(pem);
        return k;
    }

    private PublicKey publicKey() {
        PublicKey k = cachedPublicKey;
        if (k != null) return k;
        String pem = notBlank(secrets.getJwtPublicKeyPem(), "JWT public key is empty");
        cachedPublicKey = k = readPublicKeyFromPem(pem);
        return k;
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
                .signWith(privateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken(User user, String jti) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getRefresh().getExpirationDays() * 24L * 3600L);

        return Jwts.builder()
                .setIssuer(props.getRefresh().getIssuer())
                .setSubject(user.getId())
                .setId(jti) // id dokumentu refresh w Mongo
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("typ", "refresh")
                .signWith(privateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public Jws<Claims> parse(String jwt) {
        return Jwts.parser()
                .verifyWith(publicKey())
                .build()
                .parseSignedClaims(jwt);
    }

    public String getSubject(String jwt) { return parse(jwt).getPayload().getSubject(); }
    public String getJti(String jwt)      { return parse(jwt).getPayload().getId(); }
    public boolean isExpired(String jwt)  { return parse(jwt).getPayload().getExpiration().before(new Date()); }
    public String newJti()                { return UUID.randomUUID().toString(); }

    // ===== helpers =====
    private static PrivateKey readPrivateKeyFromPem(String pem) {
        try {
            byte[] der = base64Body(pem, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid RSA private key (PKCS#8)", e);
        }
    }

    private static PublicKey readPublicKeyFromPem(String pem) {
        try {
            byte[] der = base64Body(pem, "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid RSA public key (X.509)", e);
        }
    }

    private static byte[] base64Body(String pem, String begin, String end) {
        String s = pem.replace(begin, "")
                      .replace(end, "")
                      .replaceAll("\\s", "");
        return Base64.getDecoder().decode(s);
    }

    private static String notBlank(String v, String msg) {
        if (v == null || v.trim().isEmpty()) throw new IllegalStateException(msg);
        return v;
    }
}
