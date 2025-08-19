package pl.medflow.medflowbackend.domain.identity.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private long expiresIn;   // sekundy do wygaśnięcia access tokenu

}
