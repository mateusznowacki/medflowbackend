package pl.medflow.medflowbackend.domain.token;

public record JwtTokens(String accessToken, String refreshToken, long expiresInSeconds) {}
