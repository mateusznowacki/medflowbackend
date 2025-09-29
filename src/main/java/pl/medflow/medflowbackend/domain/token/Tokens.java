package pl.medflow.medflowbackend.domain.token;

public record Tokens(String accessToken,
                     String refreshToken,
                     String refreshJti,
                     long expiresInSeconds) {
}
