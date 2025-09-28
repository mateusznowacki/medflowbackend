package pl.medflow.medflowbackend.domain.auth.token;

public record Tokens(String accessToken,
                     long expiresInSeconds,
                     String refreshToken,
                     String refreshJti) {
}
