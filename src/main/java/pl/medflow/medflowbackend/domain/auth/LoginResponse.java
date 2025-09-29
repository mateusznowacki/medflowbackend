package pl.medflow.medflowbackend.domain.auth;

public record LoginResponse(
        String accessToken,
        long expiresIn,
        String tokenType
) {
    public static LoginResponse bearer(String accessToken, long expiresIn) {
        return new LoginResponse(accessToken, expiresIn, "Bearer");
    }
}
