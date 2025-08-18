package pl.medflow.medflowbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
        @CompoundIndex(name = "patient_uploaded_idx", def = "{ 'patientId': 1, 'uploadedAt': 1 }"),
        @CompoundIndex(name = "record_uploaded_idx",  def = "{ 'recordId': 1,  'uploadedAt': 1 }")
})
public class MedicalDocument {

    @Id
    private String id;

    // powiązania
    @Indexed
    private String patientId;

    @Indexed
    private String recordId;          // jeśli dokument należy do konkretnego rekordu

    // typ/klasyfikacja
    private String type;              // np. "LAB_RESULT", "SCAN", "DISCHARGE"

    // plik w S3/MinIO: przechowujemy tylko klucz
    private String fileKey;           // np. s3://bucket/key  (tu sam key)
    private String contentType;       // np. application/pdf, image/png
    private long size;                // bajty
    private String checksum;          // np. SHA-256 do weryfikacji integralności

    // dodatkowe meta
    private List<String> tags;        // opcjonalnie: tagi/foldery
    private String uploadedByUserId;  // kto wgrał
    private Instant uploadedAt;       // ustawiane zwykle przy generowaniu pre-signed URL

    @Builder.Default
    private String status = "AVAILABLE"; // np. AVAILABLE / DELETED (soft-delete)

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
