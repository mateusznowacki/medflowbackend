package pl.medflow.medflowbackend.domain.users;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{ 'email': ?0 }")
    Optional<User> findByEmail(String email);
}
