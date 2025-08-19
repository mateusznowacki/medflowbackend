package pl.medflow.medflowbackend.domain.medical_records.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(collection = "procedures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalProcedure {

    @Id
    private String id;

    @Indexed(unique = true)
    private String code;              // unikalny kod procedury (np. NFZ/ICD)

    private String name;              // nazwa procedury
    private String description;       // opis (opcjonalny)

    private int durationMinutes;      // czas trwania w minutach

    @Builder.Default
    private boolean active = true;    // czy dostępna w systemie

    @Builder.Default
    private boolean privateVisit = false; // czy dotyczy wizyt prywatnych

    private BigDecimal price;         // cena (jeśli prywatna)

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
