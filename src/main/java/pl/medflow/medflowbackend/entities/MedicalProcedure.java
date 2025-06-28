package pl.medflow.medflowbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "procedures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalProcedure {

    @Id
    private String id;

    private String name;
    private String code;
    private int durationMinutes;    }
