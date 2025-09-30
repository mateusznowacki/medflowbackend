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
import pl.medflow.medflowbackend.domain.auth.exceptions.InvalidTokenException;
import pl.medflow.medflowbackend.domain.identity.account.UserAccount;
import pl.medflow.medflowbackend.domain.identity.account.UserAccountService;
import pl.medflow.medflowbackend.infrastructure.secrets.AwsSecrets;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService implements TokenService {

    private volatile PrivateKey cachedPrivateKey;
    private volatile PublicKey cachedPublicKey;

    private final JwtProperties jwtProperties;
    private final AwsSecrets awsSecrets;
    private final UserAccountService userAccountService;
    private final RefreshTokenRepository refreshTokenRepository;

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
    private String audience() { return Optional.ofNullable(jwtProperties.getAudience()).orElse("medflow-api"); }
    private int clockSkewSeconds() { return jwtProperties.getClockSkewSeconds() > 0 ? jwtProperties.getClockSkewSeconds() : 120; }

    private String createAccessToken(UserAccount user) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessTtl());
        return Jwts.builder()
                .setSubject(user.getId())
                .setIssuer(accessIssuer())
                .setAudience(audience())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .setId(UUID.randomUUID().toString())
                .claim("role", user.getRole() != null ? user.getRole().name() : "USER")
                .claim("typ", "access")
                .signWith(privateKey(), Jwts.SIG.RS256)
                .compact();
    }

    private String createRefreshToken(UserAccount user, String jti) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(refreshTtl());
        return Jwts.builder()
                .setSubject(user.getId())
                .setIssuer(refreshIssuer())
                .setAudience(audience())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .setId(jti)
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
        String domain = jwtProperties.getCookie().getDomain();
        if (domain != null && !domain.isBlank()) {
            b.domain(domain);
        }
        return b.build();
    }

    private Claims parseAndValidate(String jwt) {
        return Jwts.parser()
                .clockSkewSeconds(clockSkewSeconds())
                .verifyWith(publicKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private void revokeAllActiveByUser(String userId) {
        var actives = refreshTokenRepository.findByUserIdAndRevokedFalse(userId);
        if (actives == null) return;
        for (var rt : actives) {
            rt.setRevoked(true);
        }
        refreshTokenRepository.saveAll(actives);
    }

    private LoginResult issueFor(UserAccount user, String parentJti) {
        String newJti = UUID.randomUUID().toString();
        // persist new refresh token allowlisted entry
        var record = RefreshToken.builder()
                .id(newJti)
                .userId(user.getId())
                .expiresAt(Instant.now().plusSeconds(refreshTtl()))
                .revoked(false)
                .parentId(parentJti)
                .build();
        refreshTokenRepository.save(record);

        String access = createAccessToken(user);
        String refresh = createRefreshToken(user, newJti);
        var body = LoginResponse.bearer(access, accessTtl());
        var cookie = buildRefreshCookie(refresh, refreshTtl());
        return new LoginResult(body, cookie);
    }

    @Override
    public LoginResult issueTokens(UserAccount user) {
        if (user == null) throw new InvalidTokenException("User is required");
        return issueFor(user, null);
    }

    @Override
    public LoginResult refresh(String refreshJwt) {
        if (refreshJwt == null || refreshJwt.isBlank()) {
            throw new InvalidTokenException("Refresh token is missing");
        }
        Claims claims = parseAndValidate(refreshJwt);
        if (!"refresh".equals(claims.get("typ"))) {
            throw new InvalidTokenException("Invalid refresh token type");
        }
        if (!refreshIssuer().equals(claims.getIssuer())) {
            throw new InvalidTokenException("Invalid refresh token issuer");
        }
        String userId = claims.getSubject();
        String jti = claims.getId();
        var entryOpt = refreshTokenRepository.findById(jti);
        if (entryOpt.isEmpty() || entryOpt.get().isRevoked() || entryOpt.get().getReplacedBy() != null) {
            // reuse or unknown token -> revoke entire family (all active for user) and force re-login
            revokeAllActiveByUser(userId);
            throw new InvalidTokenException("invalid_grant");
        }
        var current = entryOpt.get();
        var user = userAccountService.getById(userId)
                .orElseThrow(() -> new InvalidTokenException("User not found"));

        // rotate: issue new refresh token and revoke current
        String newJti = UUID.randomUUID().toString();
        var newRecord = RefreshToken.builder()
                .id(newJti)
                .userId(userId)
                .expiresAt(Instant.now().plusSeconds(refreshTtl()))
                .revoked(false)
                .parentId(current.getId())
                .build();
        refreshTokenRepository.save(newRecord);

        current.setRevoked(true);
        current.setReplacedBy(newJti);
        refreshTokenRepository.save(current);

        String access = createAccessToken(user);
        String refresh = createRefreshToken(user, newJti);
        var body = LoginResponse.bearer(access, accessTtl());
        var cookie = buildRefreshCookie(refresh, refreshTtl());
        return new LoginResult(body, cookie);
    }

    @Override
    public LoginResult refreshFromRequest(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new InvalidTokenException("No cookies found");
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
    public ResponseCookie logout(String refreshJwtFromCookie) {
        try {
            if (refreshJwtFromCookie != null && !refreshJwtFromCookie.isBlank()) {
                Claims claims = parseAndValidate(refreshJwtFromCookie);
                if ("refresh".equals(claims.get("typ")) && refreshIssuer().equals(claims.getIssuer())) {
                    String jti = claims.getId();
                    refreshTokenRepository.findById(jti).ifPresent(entry -> {
                        entry.setRevoked(true);
                        refreshTokenRepository.save(entry);
                    });
                }
            }
        } catch (Exception ignored) { }
        return buildRefreshCookie("", 0);
    }

    @Override
    public AccessClaims verifyAccessToken(String accessJwt) {
        Claims claims = parseAndValidate(accessJwt);
        if (!"access".equals(claims.get("typ"))) {
            throw new InvalidTokenException("Invalid access token type");
        }
        if (!accessIssuer().equals(claims.getIssuer())) {
            throw new InvalidTokenException("Invalid access token issuer");
        }
        String userId = claims.getSubject();
        String role = String.valueOf(claims.get("role"));
        return new AccessClaims(userId, role);
    }
}
