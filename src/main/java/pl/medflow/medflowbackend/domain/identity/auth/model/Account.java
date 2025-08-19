
package pl.medflow.medflowbackend.domain.identity.auth.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.domain.shared.enums.Role;

@Document("accounts")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {
    /**
     * Id konta = ID profilu domenowego (Doctor/Patient/MedicalStaff).
     * Dzięki temu od razu wiesz, który dokument profilu ładować.
     */
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    /** BCrypt hash */
    private String passwordHash;

    /** Kim jest użytkownik (STERUJE doborem repo przy logowaniu) */
    private Role role; // np. DOCTOR, PATIENT, MEDICAL_STAFF, ADMIN
}
