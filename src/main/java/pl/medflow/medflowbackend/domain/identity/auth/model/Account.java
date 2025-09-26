
package pl.medflow.medflowbackend.domain.identity.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
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
public class Account {

    @Id
    @EqualsAndHashCode.Include
    private ObjectId id;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;

    private Role role;
    @CreatedDate
    private java.time.Instant createdAt;

    @LastModifiedDate
    private java.time.Instant updatedAt;

}
