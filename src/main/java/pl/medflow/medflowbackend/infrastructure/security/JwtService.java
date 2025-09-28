package pl.medflow.medflowbackend.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.auth.token.TokenService;
import pl.medflow.medflowbackend.domain.auth.token.Tokens;
import pl.medflow.medflowbackend.domain.identity.role.RolePermissionService;
import pl.medflow.medflowbackend.domain.shared.user.BasicUserDto;
import pl.medflow.medflowbackend.infrastructure.secrets.AwsSecrets;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService implements TokenService {

    private final JwtProperties props;
    private final AwsSecrets secrets;
    private final RolePermissionService roles;

    private volatile PrivateKey cachedPrivateKey;
    private volatile PublicKey cachedPublicKey;


    @Override
    public String generateAccessToken(BasicUserDto user) {
        var now = Instant.now();
        var expirationMinutes = props.getAccess().getExpirationMinutes();
        var expirationDays = props.getAccess().getExpirationDays();
        var exp = now.plusSeconds(props.getAccess().getExpirationMinutes() * 60L);

        var perms = roles.getPermissions(user.role()).stream().map(Enum::name).toList();

        return io.jsonwebtoken.Jwts.builder()
                .setIssuer(props.getAccess().getIssuer())
                .setSubject(user.id())
                .setIssuedAt(java.util.Date.from(now))
                .setExpiration(java.util.Date.from(exp))
                .addClaims(java.util.Map.of(
                        "email", user.email(),
                        "role", user.role() != null ? user.role().name() : null,
                        "firstName", user.firstName(),
                        "lastName", user.lastName(),
                        "permissions", perms
                ))
                .signWith(getPrivateKey(), signatureFor(getPrivateKey()))
                .compact();
    }

    @Override
    public String generateRefreshToken(BasicUserDto user, String jti) {
        var now = Instant.now();
        var exp = now.plusSeconds(props.getRefresh().getExpirationDays() * 24L * 3600L);

        return io.jsonwebtoken.Jwts.builder()
                .setIssuer(props.getRefresh().getIssuer())
                .setSubject(user.id())
                .setId(jti)
                .setIssuedAt(java.util.Date.from(now))
                .setExpiration(java.util.Date.from(exp))
                .claim("typ", "refresh")
                .signWith(getPrivateKey(), signatureFor(getPrivateKey()))
                .compact();
    }


    @Override
    public Jws<Claims> parse(String jwt) {
        return Jwts.parser()
                .verifyWith(getPublicKey())
                .build()
                .parseClaimsJws(jwt);
    }

    @Override
    public Tokens issueTokens(BasicUserDto user) {
        return null;
    }

    @Override
    public Tokens rotateTokens(String refreshJwt) {
        return null;
    }

    @Override
    public String getSubject(String jwt) {
        return parse(jwt).getPayload().getSubject();
    }

    @Override
    public String getJti(String jwt) {
        return parse(jwt).getPayload().getId();
    }

    @Override
    public boolean isExpired(String jwt) {
        return parse(jwt).getPayload().getExpiration().before(new Date());
    }

    @Override
    public String newJti() {
        return UUID.randomUUID().toString();
    }

    private static PrivateKey readPrivateKeyFromPem(String pem) {
        byte[] der = base64Body(pem, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
        try {
            return KeyFactory.getInstance("Ed25519").generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (Exception ignored) {
        }
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(der));
        } catch (Exception e) {
            throw new IllegalStateException("Invalid private key (PKCS#8). Supported: Ed25519, RSA.", e);
        }
    }

    private static PublicKey readPublicKeyFromPem(String pem) {
        byte[] der = base64Body(pem, "-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");
        try {
            return KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(der));
        } catch (Exception ignored) {
        }
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(der));
        } catch (Exception e) {
            throw new IllegalStateException("Invalid public key (X.509). Supported: Ed25519, RSA.", e);
        }
    }

    private PrivateKey getPrivateKey() {
        if (cachedPrivateKey != null) return cachedPrivateKey;
        String pem = notBlank(secrets.getJwtPrivateKeyPem(), "JWT private key is empty");
        return cachedPrivateKey = readPrivateKeyFromPem(pem);
    }

    private PublicKey getPublicKey() {
        if (cachedPublicKey != null) return cachedPublicKey;
        String pem = notBlank(secrets.getJwtPublicKeyPem(), "JWT public key is empty");
        return cachedPublicKey = readPublicKeyFromPem(pem);
    }

    private static byte[] base64Body(String pem, String begin, String end) {
        String s = pem.replace(begin, "").replace(end, "").replaceAll("\\s", "");
        return Base64.getDecoder().decode(s);
    }

    private SignatureAlgorithm signatureFor(PrivateKey key) {
        String alg = key != null ? key.getAlgorithm() : null;
        if (alg != null) {
            if (alg.equalsIgnoreCase("Ed25519") || alg.equalsIgnoreCase("EdDSA")) {
                return Jwts.SIG.EdDSA;
            }
            if (alg.equalsIgnoreCase("RSA")) {
                return Jwts.SIG.RS256;
            }
        }
        return Jwts.SIG.RS256;
    }

    private static String notBlank(String v, String msg) {
        if (v == null || v.trim().isEmpty()) throw new IllegalStateException(msg);
        return v;
    }
}
