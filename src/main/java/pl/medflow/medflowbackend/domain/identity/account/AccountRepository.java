package pl.medflow.medflowbackend.domain.identity.account;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<UserAccount, String> {
    Optional<UserAccount> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteById(String Id);
}
