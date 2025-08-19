package pl.medflow.medflowbackend.domain.identity.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.medflow.medflowbackend.domain.identity.auth.model.RefreshTokenDocument;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshTokenDocument, String> {
    Optional<RefreshTokenDocument> findByIdAndRevokedFalse(String id);
}
