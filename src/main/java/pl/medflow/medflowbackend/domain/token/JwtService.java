// pl/medflow/medflowbackend/domain/token/JwtService.java
package pl.medflow.medflowbackend.domain.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.domain.auth.LoginResponse;
import pl.medflow.medflowbackend.domain.auth.LoginResult;
import pl.medflow.medflowbackend.domain.identity.account.UserAccount;
import pl.medflow.medflowbackend.domain.identity.account.UserAccountService;
import pl.medflow.medflowbackend.infrastructure.secrets.AwsSecrets;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService implements TokenService {

    private volatile PrivateKey cachedPrivateKey;
    private volatile PublicKey cachedPublicKey;

    private final JwtProperties jwtProperties;
    private final AwsSecrets awsSecrets;
    private final UserAccountService userAccountService;

        private PrivateKey privateKey() {
        if (cachedPrivateKey == null) {
            synchronized (this) {
                if (cachedPrivateKey == null) {
                    cachedPrivateKey = loadPrivateKeyFromPem(awsSecrets.getJwtPrivateKeyPem());
                }
            }
        }
        return cachedPrivateKey;
    }

    private PublicKey publicKey() {
        if (cachedPublicKey == null) {
            synchronized (this) {
                if (cachedPublicKey == null) {
                    cachedPublicKey = loadPublicKeyFromPem(awsSecrets.getJwtPublicKeyPem());
                }
            }
        }
        return cachedPublicKey;
    }

    private static PrivateKey loadPrivateKeyFromPem(String pem) {
        try {
            String content = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] bytes = Base64.getDecoder().decode(content);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid private key PEM", e);
        }
    }

    private static PublicKey loadPublicKeyFromPem(String pem) {
        try {
            String content = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] bytes = Base64.getDecoder().decode(content);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid public key PEM", e);
        }
    }

    private String cookieName() { return Optional.ofNullable(jwtProperties.getCookie().getName()).orElse("refreshToken"); }
    private String cookiePath() { return Optional.ofNullable(jwtProperties.getCookie().getPath()).orElse("/"); }
    private String cookieSameSite() { return Optional.ofNullable(jwtProperties.getCookie().getSameSite()).orElse("Lax"); }
    private boolean cookieSecure() { return jwtProperties.getCookie().isSecure(); }

    private int accessTtl() { return jwtProperties.getAccess().accessExpirationSeconds(); }
    private int refreshTtl() { return jwtProperties.getRefresh().refreshExpirationSeconds(); }
    private String accessIssuer() { return Optional.ofNullable(jwtProperties.getAccess().getIssuer()).orElse("medflow"); }
    private String refreshIssuer() { return Optional.ofNullable(jwtProperties.getRefresh().getIssuer()).orElse("medflow"); }

    private String createAccessToken(UserAccount user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTtl());
        return Jwts.builder()
                .setSubject(user.getId())
                .claim("role", user.getRole() != null ? user.getRole().name() : "USER")
                .setIssuer(accessIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("typ", "access")
                .signWith(privateKey(), Jwts.SIG.RS256)
                .compact();
    }

    private String createRefreshToken(UserAccount user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshTtl());
        return Jwts.builder()
                .setSubject(user.getId())
                .setIssuer(refreshIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("typ", "refresh")
                .signWith(privateKey(), Jwts.SIG.RS256)
                .compact();
    }

    private ResponseCookie buildRefreshCookie(String token, long maxAgeSeconds) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(cookieName(), token)
                .httpOnly(true)
                .secure(cookieSecure())
                .path(cookiePath())
                .maxAge(maxAgeSeconds);
        String sameSite = cookieSameSite();
        if (sameSite != null && !sameSite.isBlank()) {
            b.sameSite(sameSite);
        }
        return b.build();
    }

    private Claims parseAndValidate(String jwt) {
        return Jwts.parser()
                .verifyWith(publicKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    @Override
    public LoginResult login(UserAccount user, String rawPassword) {
        if (user == null || rawPassword == null || !userAccountService.verifyPassword(user.getEmail(), rawPassword)) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        String access = createAccessToken(user);
        String refresh = createRefreshToken(user);
        var body = LoginResponse.bearer(access, accessTtl());
        var cookie = buildRefreshCookie(refresh, refreshTtl());
        return new LoginResult(body, cookie);
    }

    @Override
    public LoginResult refresh(String refreshJwt) {
        if (refreshJwt == null || refreshJwt.isBlank()) {
            throw new IllegalArgumentException("Refresh token is missing");
        }
        Claims claims = parseAndValidate(refreshJwt);
        if (!"refresh".equals(claims.get("typ"))) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        String userId = claims.getSubject();
        var user = userAccountService.getById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String access = createAccessToken(user);
        String refresh = createRefreshToken(user);
        var body = LoginResponse.bearer(access, accessTtl());
        var cookie = buildRefreshCookie(refresh, refreshTtl());
        return new LoginResult(body, cookie);
    }

    @Override
    public LoginResult refreshFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new IllegalArgumentException("No cookies found");
        }
        String token = null;
        for (Cookie c : request.getCookies()) {
            if (cookieName().equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }
        return refresh(token);
    }

    @Override
    public ResponseCookie logout() {
        return buildRefreshCookie("", 0);
    }

    @Override
    public AccessClaims verifyAccessToken(String accessJwt) {
        Claims claims = parseAndValidate(accessJwt);
        if (!"access".equals(claims.get("typ"))) {
            throw new IllegalArgumentException("Invalid access token");
        }
        String userId = claims.getSubject();
        String role = String.valueOf(claims.get("role"));
        return new AccessClaims(userId, role);
    }
}
