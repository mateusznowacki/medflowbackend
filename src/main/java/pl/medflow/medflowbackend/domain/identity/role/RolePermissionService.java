package pl.medflow.medflowbackend.domain.identity.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.medflow.medflowbackend.domain.shared.enums.Role;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RolePermissionsRepository repository;

    public Set<Permission> getPermissions(Role role) {
        if (role == null) return Set.of();
        return repository.findByRole(role)
                .map(rp -> rp.getPermissions() == null
                        ? Set.<Permission>of()
                        : EnumSet.copyOf(rp.getPermissions()))
                .orElse(Set.of());
    }

    
    @Transactional
    public RolePermissions upsert(Role role, List<Permission> permissions, String description) {
        if (role == null) {
            throw new IllegalArgumentException("Role must not be null");
        }
        List<Permission> list = sanitize(permissions);

        RolePermissions entity = repository.findByRole(role)
                .map(existing -> {
                    existing.setPermissions(list);
                    existing.setDescription(description);
                    return existing;
                })
                .orElse(RolePermissions.builder()
                        .role(role)
                        .permissions(list)
                        .description(description)
                        .build());

        return repository.save(entity);
    }
    
    @Transactional
        public RolePermissions addPermission(Role role, Permission permission) {
        if (role == null) throw new IllegalArgumentException("Role must not be null");
        if (permission == null) return getOrCreate(role);

        RolePermissions entity = repository.findByRole(role)
                .orElseGet(() -> RolePermissions.builder()
                        .role(role)
                        .permissions(new ArrayList<>())
                        .build());

        List<Permission> perms = entity.getPermissions();
        if (perms == null) {
            perms = new ArrayList<>();
            entity.setPermissions(perms);
        }
        if (!perms.contains(permission)) {
            perms.add(permission);
        }
        return repository.save(entity);
    }

    @Transactional
       public RolePermissions removePermission(Role role, Permission permission) {
        if (role == null) throw new IllegalArgumentException("Role must not be null");
        if (permission == null) return getOrCreate(role);

        RolePermissions entity = repository.findByRole(role)
                .orElseGet(() -> RolePermissions.builder()
                        .role(role)
                        .permissions(new ArrayList<>())
                        .build());

        List<Permission> perms = entity.getPermissions();
        if (perms != null && !perms.isEmpty()) {
            perms.removeIf(p -> p == permission);
        }
        return repository.save(entity);
    }

    @Transactional
    public RolePermissions clearPermissions(Role role) {
        if (role == null) throw new IllegalArgumentException("Role must not be null");
        RolePermissions entity = repository.findByRole(role)
                .orElseGet(() -> RolePermissions.builder()
                        .role(role)
                        .permissions(new ArrayList<>())
                        .build());
        entity.setPermissions(new ArrayList<>());
        return repository.save(entity);
    }

    @Transactional
    public RolePermissions updateDescription(Role role, String description) {
        if (role == null) throw new IllegalArgumentException("Role must not be null");
        RolePermissions entity = repository.findByRole(role)
                .orElseGet(() -> RolePermissions.builder()
                        .role(role)
                        .permissions(new ArrayList<>())
                        .build());
        entity.setDescription(description);
        return repository.save(entity);
    }

    private RolePermissions getOrCreate(Role role) {
        return repository.findByRole(role)
                .orElseGet(() -> repository.save(RolePermissions.builder()
                        .role(role)
                        .permissions(new ArrayList<>())
                        .build()));
    }

    private List<Permission> sanitize(List<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) return new ArrayList<>();
        List<Permission> out = new ArrayList<>();
        for (Permission p : permissions) {
            if (p != null && !out.contains(p)) {
                out.add(p);
            }
        }
        return out;
    }
}