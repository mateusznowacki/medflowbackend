package pl.medflow.medflowbackend.domain.identity.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pl.medflow.medflowbackend.domain.identity.user.dto.BasicUser;
import pl.medflow.medflowbackend.domain.shared.enums.Permission;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private long expiresIn;   // sekundy do wygaśnięcia access tokenu
    private BasicUser user;
    private List<Permission> permissions;// opcjonalnie, patrz niżej
}
