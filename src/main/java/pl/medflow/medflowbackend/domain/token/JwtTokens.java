package pl.medflow.medflowbackend.domain.token;

public record JwtTokens(String accessToken,
                        String refreshToken,
                        String refreshJti,
                        long expiresInSeconds) {
}
