package pl.medflow.medflowbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import pl.medflow.medflowbackend.enums.Role;

@Data
@AllArgsConstructor
@Builder
public class BasicUser {
    private String id;
    private Role role;
    private String firstName;
    private String lastName;
    private String email;
}