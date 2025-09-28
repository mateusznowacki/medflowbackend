package pl.medflow.medflowbackend.domain.shared.user;

import pl.medflow.medflowbackend.domain.shared.enums.Role;

public record BasicUserDto(
        String id,
        String email,
        Role role,
        String firstName,
        String lastName,
        String phoneNumber
) {
}
