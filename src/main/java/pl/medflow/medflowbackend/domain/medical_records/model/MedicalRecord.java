package pl.medflow.medflowbackend.domain.medical_records.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
        @CompoundIndex(name = "patient_visit_idx", def = "{ 'patientId': 1, 'visitAt': 1 }"),
        @CompoundIndex(name = "doctor_visit_idx", def = "{ 'doctorId': 1,  'visitAt': 1 }")
})
public class MedicalRecord {

    @Id
    private String id;

    @Indexed
    private String patientId;

    @Indexed
    private String doctorId;

    private String appointmentId;   // powiązanie z wizytą (jeśli jest)

    private Instant visitAt;        // data/godzina wizyty (UTC)

    private List<String> procedureIds; // referencje do MedicalProcedure.id

    private List<String> documentIds; // referencje do MedicalDocument.id

    @Version
    private Long version;           // optimistic locking przy edycjach

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
