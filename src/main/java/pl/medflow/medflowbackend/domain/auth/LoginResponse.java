package pl.medflow.medflowbackend.domain.auth;


public record LoginResponse(
        String accessToken,
        long expiresIn

) {
}
