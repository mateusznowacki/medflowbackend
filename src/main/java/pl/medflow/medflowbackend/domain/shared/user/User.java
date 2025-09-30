package pl.medflow.medflowbackend.domain.shared.user;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;

import java.time.Instant;

@TypeAlias("user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class User {

    @Id
    private String id;

    @Pattern(regexp = "^[0-9+\\- ]{7,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    private boolean active = true;

    private boolean emailVerified = false;

    private Instant lastLoginAt;

    private Instant passwordUpdatedAt;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
