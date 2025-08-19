package pl.medflow.medflowbackend.domain.identity.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.medflow.medflowbackend.domain.identity.auth.model.Account;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);
}
