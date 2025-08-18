package pl.medflow.medflowbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "procedures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalProcedure {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String code;              // unikalny kod procedury (np. katalog NFZ/ICD)

    private String description;       // opcjonalnie, krótki opis

    private int durationMinutes;      // czas trwania (używany przy slotach)

    @Builder.Default
    private boolean active = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
