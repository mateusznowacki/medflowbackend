package pl.medflow.medflowbackend.todo.patient_management.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.domain.shared.embedded.Address;
import pl.medflow.medflowbackend.domain.shared.enums.Gender;
import pl.medflow.medflowbackend.domain.shared.user.model.User;

import java.time.LocalDate;
import java.time.Instant;
import java.util.List;

@Document(collection = "patients")
@TypeAlias("patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@CompoundIndex(name = "email_unique_pat", def = "{ 'email': 1 }", unique = true)
public class Patient extends User {

    @Indexed(unique = true, sparse = true)
    private String nationalId; // PESEL lub inny identyfikator

    private Gender gender; // (zgodnie z Twoją decyzją: MALE/FEMALE)
    private LocalDate dateOfBirth;
    private Address address;

    // Kontakt w nagłych wypadkach
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Zgody/system
    private boolean consentGiven;
    private Instant termsAcceptedAt;
    private Instant privacyPolicyAcceptedAt;

    // Dane medyczne
    private List<String> allergies;
    private List<String> medications;
    private List<String> chronicDiseases;

    private String familyDoctorId; // referencja do Doctor.id
}
