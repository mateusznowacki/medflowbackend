package pl.medflow.medflowbackend.domain.token;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenDocument {

    @Id
    private String id;

    private String userId;

    @Indexed(expireAfter = "0s")
    private Instant expiresAt;

    private boolean revoked;
    private String replacedBy;
}
//todo consider using this or delete it