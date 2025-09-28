package pl.medflow.medflowbackend.domain.identity.reposiotry;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.medflow.medflowbackend.domain.identity.model.RolePermissions;
import pl.medflow.medflowbackend.domain.shared.enums.Role;

import java.util.Optional;

public interface RolePermissionsRepository extends MongoRepository<RolePermissions, String> {
    Optional<RolePermissions> findByRole(Role role);
}
