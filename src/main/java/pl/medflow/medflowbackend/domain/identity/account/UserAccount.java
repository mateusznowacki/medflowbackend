
package pl.medflow.medflowbackend.domain.identity.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.domain.shared.enums.Role;

@Document("accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Email
    @NotBlank
    @Indexed(unique = true)
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String passwordHash;

    private Role role;

    @CreatedDate
    private java.time.Instant createdAt;

    @LastModifiedDate
    private java.time.Instant updatedAt;


}



