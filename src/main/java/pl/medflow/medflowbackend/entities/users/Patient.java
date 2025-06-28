package pl.medflow.medflowbackend.entities.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.entities.embedded.Address;
import pl.medflow.medflowbackend.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Document(collection = "users")
@TypeAlias("patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends User {
  // Identyfikacja i dane osobowe
    private String nationalId;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Address address;

    // Kontakt w nagłych wypadkach
    private String emergencyContactName;
    private String emergencyContactPhone;

    // Informacje systemowe
    private boolean emailVerified;
    private boolean consentGiven;
    private LocalDateTime termsAcceptedAt;
    private LocalDateTime privacyPolicyAcceptedAt;

    // Dane medyczne
    private List<String> allergies;
    private List<String> medications;
    private List<String> chronicDiseases;
    private String familyDoctorId;
}
