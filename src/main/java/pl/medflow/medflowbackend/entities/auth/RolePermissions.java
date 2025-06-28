package pl.medflow.medflowbackend.entities.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.enums.Role;

import java.util.List;

@Document(collection = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissions {

    @Id
    private String id;

    private Role role;

    private List<String> permissions;
}