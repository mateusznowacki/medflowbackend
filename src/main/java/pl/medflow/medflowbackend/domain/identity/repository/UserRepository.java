package pl.medflow.medflowbackend.domain.identity.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pl.medflow.medflowbackend.domain.identity.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{ 'email': ?0 }")
    Optional<User> findByEmail(String email);
}
