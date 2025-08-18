
package pl.medflow.medflowbackend.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.medflow.medflowbackend.auth.RefreshTokenDocument;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshTokenDocument, String> {
    Optional<RefreshTokenDocument> findByIdAndRevokedFalse(String id);
}
