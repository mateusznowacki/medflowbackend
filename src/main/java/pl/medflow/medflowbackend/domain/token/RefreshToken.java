package pl.medflow.medflowbackend.domain.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshToken {
    @Id
    private String id; // jti
    private String userId;
    private Instant expiresAt;
    private boolean revoked;
    private String replacedBy; // jti of the new token
    private String parentId;   // previous jti (for family tracking)
}
