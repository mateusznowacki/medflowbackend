package pl.medflow.medflowbackend.domain.facility_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.medflow.medflowbackend.domain.shared.embedded.RoomLocation;

import java.time.Instant;
import java.util.List;

@Document(collection = "facilities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facility {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    // Możesz podmienić na Address VO, jeśli chcesz (masz już Address w projekcie)
    private String address;

    private List<RoomLocation> rooms;

    // np. PL-DS (Dolnośląskie) lub inny własny podział
    private String regionCode;

    // ważne dla konwersji godzin wizyt (np. "Europe/Warsaw")
    private String timezone;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
