package pl.medflow.medflowbackend.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(name = "facility_name_idx", def = "{ 'facilityId': 1, 'name': 1 }", unique = true)
public class Department {

    @Id
    private String id;

    @Indexed
    private String facilityId;      // do której placówki należy oddział

    private String name;            // np. Kardiologia
    private String code;            // np. wewnętrzny kod oddziału (unikat w placówce)

    private String headDoctorId;    // ordynator (opcjonalnie)
    private String phone;           // kontakt do oddziału (opcjonalnie)

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
