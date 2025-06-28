package pl.medflow.medflowbackend.entities.users;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import pl.medflow.medflowbackend.enums.Role;

import java.time.Instant;

@TypeAlias("user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public abstract class User {

    @Id
    private String id;
    private Role role;
    private String firstName;
    private String lastName;

    @Email
    private String email;
    private String phoneNumber;

    private String password;
    private boolean active = true;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private Instant updatedAt = Instant.now();

}