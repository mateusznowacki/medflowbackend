package pl.medflow.medflowbackend.domain.identity.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "refresh_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshTokenDocument {

    @Id
    private String id;

    private String userId;

    // <- TTL będzie liczony na podstawie tej daty
    @Indexed(expireAfter = "0s")   // NOWE podejście
    private Instant expiresAt;

    private boolean revoked;
    private String replacedBy;
}
