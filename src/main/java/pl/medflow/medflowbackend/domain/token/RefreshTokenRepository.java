package pl.medflow.medflowbackend.domain.token;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshTokenDocument, String> {
    Optional<RefreshTokenDocument> findByIdAndRevokedFalse(String id);
}
//todo consider using this or delete it