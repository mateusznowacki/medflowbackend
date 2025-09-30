package pl.medflow.medflowbackend.domain.token;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    List<RefreshToken> findByUserIdAndRevokedFalse(String userId);
}
