package pl.medflow.medflowbackend.domain.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.identity.account.UserAccount;
import pl.medflow.medflowbackend.domain.identity.account.UserAccountService;
import pl.medflow.medflowbackend.domain.identity.role.RolePermissionService;
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

    private volatile PrivateKey cachedPrivateKey;
    private volatile PublicKey cachedPublicKey;
    private final JwtProperties jwtProperties;
    private final AwsSecrets awsSecrets;

    private final RolePermissionService rolePermissionService;
    private final UserAccountService userAccountService;

    @Override
    public String generateAccessToken(UserAccount user) {
        var now = Instant.now();
        var exp = now.plusSeconds(jwtProperties.getAccess().accessExpirationSeconds());

        var perms = rolePermissionService.getPermissions(user.getRole()).stream().map(Enum::name).toList();

        return io.jsonwebtoken.Jwts.builder()
                .setIssuer(jwtProperties.getAccess().getIssuer())
                .setSubject(user.getId())
                .setIssuedAt(java.util.Date.from(now))
                .setExpiration(java.util.Date.from(exp))
                .addClaims(java.util.Map.of(
                        "email", user.getEmail(),
                        "role", user.getRole() != null ? user.getRole().name() : null,
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName(),
                        "permissions", perms
                ))
                .signWith(getPrivateKey(), signatureFor(getPrivateKey()))
                .compact();
    }

    @Override
    public String generateRefreshToken(UserAccount user, String jti) {
        var now = Instant.now();
        var exp = now.plusSeconds(jwtProperties.getRefresh().refreshExpirationSeconds());

        return io.jsonwebtoken.Jwts.builder()
                .setIssuer(jwtProperties.getRefresh().getIssuer())
                .setSubject(user.getId())
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
    public JwtTokens issueTokens(UserAccount user) {
        String accessToken = generateAccessToken(user);
        String jti = newJti();
        String refreshToken = generateRefreshToken(user, jti);
        long expiresIn = jwtProperties.getAccess().accessExpirationSeconds();
        return new JwtTokens(accessToken, refreshToken, jti, expiresIn);
    }

    @Override
    public JwtTokens rotateTokens(String refreshJwt) {
        Jws<Claims> claims = parse(refreshJwt);
        if (isExpired(refreshJwt)) {
            throw new IllegalStateException("Refresh token expired");
        }
        // Ensure it's a refresh token
        Claims payload = claims.getPayload();
        Object typ = payload.get("typ");
        if (typ != null && !"refresh".equals(typ.toString())) {
            throw new IllegalArgumentException("Invalid token type");
        }

        String jti = newJti();
        String subject = payload.getSubject();

       var user = userAccountService.getById(subject);
        String accessToken = generateAccessToken(user.orElse(null));
        String refreshToken = generateRefreshToken(user.orElse(null), jti);
        long expiresIn = jwtProperties.getAccess().accessExpirationSeconds();
        return new JwtTokens(accessToken, refreshToken, jti, expiresIn);
    }

@Override
    public int getRefreshExpirationSeconds() {
        return jwtProperties.getRefresh().refreshExpirationSeconds();
    }

    @Override
    public String getCookieName() {
        return jwtProperties.getCookie().getName();
    }

    @Override
    public boolean isCookieSecure() {
        return jwtProperties.getCookie().isSecure();
    }

    @Override
    public String getCookieSameSite() {
        return jwtProperties.getCookie().getSameSite();
    }

    @Override
    public String getCookiePath() {
        return jwtProperties.getCookie().getPath();
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
        String pem = notBlank(awsSecrets.getJwtPrivateKeyPem(), "JWT private key is empty");
        return cachedPrivateKey = readPrivateKeyFromPem(pem);
    }

    private PublicKey getPublicKey() {
        if (cachedPublicKey != null) return cachedPublicKey;
        String pem = notBlank(awsSecrets.getJwtPublicKeyPem(), "JWT public key is empty");
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
